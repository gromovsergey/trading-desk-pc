package com.foros.reporting.tools.query.cache;

import com.foros.cache.generic.CacheRegion;
import com.foros.cache.generic.EntityIdTag;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.rowsource.RowSource;
import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import com.foros.reporting.tools.query.Query;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;
import org.springframework.jdbc.core.ResultSetExtractor;

public class CachedQuery implements Query {
    private static final Logger logger = Logger.getLogger(CachedQuery.class.getName());

    private Query query;
    private final EntityManager em;
    private CacheRegion cacheRegion;
    private int maxCachedRows;

    private Map<String, Object> parameters = new TreeMap<>();
    private List<Object> tags = new ArrayList<>();

    public CachedQuery(EntityManager em, CacheRegion cacheRegion, Query query, int maxCachedRows) {
        this.em = em;
        this.cacheRegion = cacheRegion;
        this.query = query;
        this.maxCachedRows = maxCachedRows;
    }

    @Override
    public CachedQuery cached() {
        return this;
    }

    @Override
    public CachedQuery parameter(String name, Object value) {
        query.parameter(name, value);
        return cacheParameter(name, value);
    }

    @Override
    public CachedQuery parameter(String name, Object value, UserType type) {
        query.parameter(name, value, type);
        return cacheParameter(name, value);
    }

    @Override
    public CachedQuery parameter(String name, Object value, Type type) {
        query.parameter(name, value, type);
        return cacheParameter(name, value);
    }

    @Override
    public CachedQuery parameter(String name, Object value, int sqlType) {
        query.parameter(name, value, sqlType);
        return cacheParameter(name, value);
    }

    public CachedQuery tag(Class<?> type, Long id) {
        tags.add(EntityIdTag.create(em, type, id));
        return this;
    }

    public CachedQuery tags(Class<?> type, Collection<Long> ids) {
        for (Long id : ids) {
            tag(type, id);
        }

        return this;
    }

    @Override
    public CachedQuery readerRegistry(ResultSetValueReaderRegistry readerRegistry) {
        query.readerRegistry(readerRegistry);
        return this;
    }

    @Override
    public void execute(ResultHandler handler, IterationStrategy iterationStrategy) {
        RowSource source = cacheRegion.get(createKeyByParameters());

        if (source != null) {
            iterationStrategy.process(source, handler);
        } else {
            query.execute(wrapForStore(handler), iterationStrategy);
        }
    }

    @Override
    public void execute(ResultHandler handler, MetaData metaData) {
        RowSource source = cacheRegion.get(createKeyByParameters());

        if (source != null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Using cached result");
            }
            new SimpleIterationStrategy(metaData).process(source, handler);
        } else {
            query.execute(wrapForStore(handler), metaData);
        }
    }

    @Override
    public void execute(ResultSetExtractor work) {
        throw new UnsupportedOperationException();
    }

    private ResultHandler wrapForStore(ResultHandler handler) {
        return new CacheResultHandler(cacheRegion, createKeyByParameters(), tags, handler, maxCachedRows);
    }

    private Object createKeyByParameters() {
        return parameters;
    }

    private CachedQuery cacheParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }
}
