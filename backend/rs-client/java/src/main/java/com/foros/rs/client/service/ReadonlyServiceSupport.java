package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.PagingSelectorContainerAccessor;
import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.util.Fetcher;
import com.foros.rs.client.util.FetcherImplementer;
import com.foros.rs.client.util.PagingAccessor;
import com.foros.rs.client.util.UrlBuilder;

public abstract class ReadonlyServiceSupport<S extends PagingSelectorContainer, E extends EntityBase>
        implements FetcherBuilder<S, E> {

    protected final RsClient rsClient;
    protected final String path;

    private final PagingAccessor<S> pagingAccessor;

    protected ReadonlyServiceSupport(RsClient rsClient, String path) {
        this.rsClient = rsClient;
        this.path = path;
        this.pagingAccessor = pagingAccessor();
    }

    protected final PagingAccessor<S> pagingAccessor() {
        return new PagingSelectorContainerAccessor<>();
    }

    protected Result<E> get(String uri) {
        return rsClient.get(uri);
    }


    protected final Result<E> get(S selector) {
        String uri = UrlBuilder.path(path)
                .addQueryEntity(selector)
                .build();

        return rsClient.get(uri);
    }

    @Override
    public Fetcher<S, E> fetcher() {
        return new Fetcher<S, E>(pagingAccessor, new FetcherImplementer<S, E>() {
            @Override
            public Result<E> fetch(S selector) {
                return get(selector);
            }
        });
    }
}
