package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.model.operation.Result;

import java.util.List;


public class Fetcher<S, E extends EntityBase> {

    private static final int MAX_PAGE_SIZE = 500;

    private final PagingAccessor<S> pagingAccessor;
    private final FetchOperationEvents<E> eventHandler;
    private final FetcherImplementer<S, E> fetcherImplementer;
    private final int pageSize;

    public Fetcher(PagingAccessor<S> pagingAccessor, FetcherImplementer<S, E>  fetcherImplementer) {
        this(pagingAccessor, fetcherImplementer, new DoNothingFetchOperationEvents<E>(), MAX_PAGE_SIZE);
    }

    public Fetcher(
            PagingAccessor<S> pagingAccessor,
            FetcherImplementer<S, E>  fetcherImplementer,
            FetchOperationEvents<E> eventHandler,
            int pageSize
    ) {

        this.pagingAccessor = pagingAccessor;
        this.fetcherImplementer = fetcherImplementer;
        this.eventHandler = eventHandler;
        this.pageSize = pageSize;
    }

    public Fetcher<S, E> withEventHandler(FetchOperationEvents<E> eventHandler) {
        return new Fetcher<>(this.pagingAccessor, this.fetcherImplementer, eventHandler, this.pageSize);
    }

    public Fetcher<S, E> withPageSize(int pageSize) {
        return new Fetcher<>(this.pagingAccessor, this.fetcherImplementer, this.eventHandler, pageSize);
    }

    public List<E> fetch(S selector) {
        AccumulateResultCallback<E> accumulateResultCallback = new AccumulateResultCallback<E>();
        fetch(selector, accumulateResultCallback);
        return accumulateResultCallback.getEntities();
    }

    public List<E> all() {
        return fetch(null);
    }

    public void fetch(S selector, ResultCallback<E> resultCallback) {
        PagingSelector original = pagingAccessor.getPaging(selector);

        long originalCount;
        PagingSelector pagingSelector = new PagingSelector();
        if (original != null && original.getFirst() != null) {
            pagingSelector.setFirst(original.getFirst());
        } else {
            pagingSelector.setFirst(0L);
        }

        if (original != null && original.getCount() != null) {
            // fetch maximum original.getCount() records
            originalCount = original.getCount();
        } else {
            // fetch all
            originalCount = Integer.MAX_VALUE;
        }

        int count = 0;

        eventHandler.onBefore();

        while (true) {
            pagingSelector.setCount(Math.min(pageSize, originalCount - count));
            selector = pagingAccessor.setPaging(selector, pagingSelector);

            Result<E> result = fetcherImplementer.fetch(selector);

            assertNotNull("result", result);
            assertNotNull("result.paging", result.getPaging());
            assertNotNull("result.paging.count", result.getPaging().getCount());
            assertNotNull("result.paging.first", result.getPaging().getFirst());
            assertNotNull("result.entities", result.getEntities());

            if (result.getEntities().size() == 0) {
                break;
            }

            eventHandler.onResult(result);
            resultCallback.result(result.getEntities());
            count += result.getEntities().size();

            if (result.getEntities().size() < result.getPaging().getCount()) {
                break;
            }

            if (count >= originalCount) {
                break;
            }

            pagingSelector.setFirst(pagingSelector.getFirst() + result.getEntities().size());
        }

        eventHandler.onAfter(count);

        pagingAccessor.setPaging(selector, original);
    }

    private void assertNotNull(String str, Object val) {
        if (val == null) {
            throw new NullPointerException(str);
        }
    }

    public static class DoNothingFetchOperationEvents<E extends EntityBase> implements FetchOperationEvents<E> {
        @Override
        public void onBefore() {
            // do nothing
        }

        @Override
        public void onResult(Result<E> result) {

        }


        @Override
        public void onAfter(int count) {
            // do nothing
        }
    }
}
