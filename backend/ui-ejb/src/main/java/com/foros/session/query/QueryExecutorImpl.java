package com.foros.session.query;

import com.foros.session.bulk.Paging;
import com.foros.session.query.criterion.AnyCriterion;
import com.foros.util.command.HibernateWork;
import com.foros.util.command.executor.HibernateWorkExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.JDBCException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

class QueryExecutorImpl implements QueryExecutor {
    private static final Logger logger = Logger.getLogger(QueryExecutorImpl.class.getName());

    private static interface FetchingStrategy {

        <T> PartialList<T> partialList(int from, int count);

        int count();

    }

    private class DefaultFetchingStrategy implements FetchingStrategy {

        @Override
        public <T> PartialList<T> partialList(int from, int count) {
            if (!query.getCriteria().isUseIdCriteria()) {
                return simplePartialList(from, count);
            }
            return compositePartialList(from, count);
        }

        private <T> PartialList<T> simplePartialList(int from, int count) {
            int size = count();
            List<T> result = createCriteria()
                .setFirstResult(from)
                .setMaxResults(count)
                .list();

            return new PartialList<>(size, from, result);
        }

        private <T> PartialList<T> compositePartialList(int from, int count) {
            int size = count();
            List<Long> ids = createIdCriteria()
                .setFirstResult(from)
                .setMaxResults(count)
                .list();

            if (ids.isEmpty()) {
                return PartialList.emptyList();
            }

            List<T> result = createCriteria()
                .add(AnyCriterion.anyId("this.id", ids))
                .list();

            return new PartialList<>(size, from, result);
        }

        @Override
        public int count() {
            Number num = (Number) createCountCriteria().setFirstResult(0)
                .setMaxResults(Integer.MAX_VALUE)
                .setProjection(Projections.count("id"))
                .uniqueResult();
            return num.intValue();
        }

    }

    private class ScrollableFetchingStrategy implements FetchingStrategy {

        @Override
        public <T> PartialList<T> partialList(int from, int count) {
            ScrollableResults results = createCriteria()
                .setFetchSize(count)
                .scroll();

            List<T> records = new ArrayList<T>();

            if (results.first()) {
                if (results.scroll(from)) {
                    for (int i = 0; i < count; i++) {
                        records.add((T) results.get(0));
                        if (!results.next()) {
                            break;
                        }
                    }
                }
            }

            return new PartialList<T>(getTotal(results), from, records);
        }

        private int getTotal(ScrollableResults results) {
            results.last();
            return results.getRowNumber();
        }

        @Override
        public int count() {
            ScrollableResults results = createCriteria().scroll();
            return getTotal(results);
        }

    }

    private class NoCountFetchingStrategy extends DefaultFetchingStrategy {
        @Override
        public int count() {
            return -1;
        }
    }

    private FetchingStrategy fetchingStrategy = new DefaultFetchingStrategy();
    private HibernateWorkExecutorService executorService;
    private BusinessQuery query;

    public QueryExecutorImpl(HibernateWorkExecutorService executorService, BusinessQuery query) {
        this.executorService = executorService;
        this.query = query;
    }

    private Criteria createCriteria() {
        return executorService.execute(new HibernateWork<Criteria>() {
            @Override
            public Criteria execute(Session session) {
                return query
                    .preExecute()
                    .getCriteria()
                    .getDataCriteria()
                    .getExecutableCriteria(session);
            }
        });
    }

    private Criteria createCountCriteria() {
        return executorService.execute(new HibernateWork<Criteria>() {
            @Override
            public Criteria execute(Session session) {
                /**
                 * It is important to add additional order by "id"
                 * to get stable results during pagination
                 */
                return query
                    .getCriteria()
                    .getCountCriteria()
                    .getExecutableCriteria(session);
            }
        });
    }

    private Criteria createIdCriteria() {
        return executorService.execute(new HibernateWork<Criteria>() {
            @Override
            public Criteria execute(Session session) {
                /**
                 * It is important to add additional order by "id"
                 * to get stable results during pagination
                 */
                return query
                    .getCriteria()
                    .getIdCriteria().addOrder(Order.asc("id"))
                    .getExecutableCriteria(session);
            }
        });
    }

    @Override
    public <T> List<T> list() {
        try {
            return createCriteria().list();
        } catch (JDBCException e) {
            logger.warning("Exception in query: " + e.getSQL());
            throw e;
        }
    }

    @Override
    public <T> PartialList<T> partialList(int from, int count) {
        checkCount();
        FetchingStrategy fetchingStrategy2 = this.fetchingStrategy;
        try {
            return fetchingStrategy2.partialList(from, count);
        } catch (JDBCException e) {
            logger.warning("Exception in query: " + e.getSQL());
            throw e;
        }
    }

    private void checkCount() {
        if (!query.getCriteria().isUseCountCriteria()) {
            noCount();
        }
    }

    @Override
    public <T> PartialList<T> partialList(Paging paging) {
        if (paging == null) {
            paging = new Paging();
        } else if (paging.getCount() > Paging.MAX_PAGE_SIZE) {
            paging.setCount(Paging.MAX_PAGE_SIZE);
        }

        Integer firstRow = paging.getFirst();
        Integer numberOfRows = paging.getCount();
        PartialList<T> partialList = partialList(firstRow.intValue(), numberOfRows.intValue());

        partialList.setPaging(paging);
        return partialList;
    }

    @Override
    public int count() {
        checkCount();
        try {
            return this.fetchingStrategy.count();
        } catch (JDBCException e) {
            logger.warning("Exception in query: " + e.getSQL());
            throw e;
        }
    }

    @Override
    public QueryExecutor scrollable() {
        this.fetchingStrategy = new ScrollableFetchingStrategy();
        return this;
    }

    @Override
    public QueryExecutor noCount() {
        this.fetchingStrategy = new NoCountFetchingStrategy();
        return this;
    }
}
