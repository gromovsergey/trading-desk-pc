package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.data.JAXBEntity;
import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.PagingSelectorContainerAccessor;
import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.util.Fetcher;
import com.foros.rs.client.util.FetcherImplementer;
import com.foros.rs.client.util.PagingAccessor;
import com.foros.rs.client.util.Uploader;
import com.foros.rs.client.util.UploaderImplementer;
import com.foros.rs.client.util.UrlBuilder;

public abstract class EntityServiceSupport<E extends EntityBase, S extends PagingSelectorContainer> implements
        FetcherBuilder<S, E>,
        UploaderBuilder<E> {

    private final String path;
    private final PagingAccessor<S> pagingAccessor;
    private final String name = getClass().getSimpleName();

    protected final RsClient rsClient;

    protected EntityServiceSupport(RsClient rsClient, String path) {
        this.rsClient = rsClient;
        this.path = path;
        this.pagingAccessor = new PagingSelectorContainerAccessor<>();
    }

    protected Result<E> get(String uri) {
        return rsClient.get(uri);
    }


    public final Result<E> get(S selector) {
        String uri = UrlBuilder.path(path)
                .addQueryEntity(selector)
                .build();

        return rsClient.get(uri);
    }

    public final OperationsResult perform(Operations<E> channelOperations) {
        return rsClient.post(path, new JAXBEntity(channelOperations));
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

    @Override
    public Uploader<E> uploader() {
        return new Uploader<E>(name).withUploader(new UploaderImplementer<E>() {
                    @Override
                    public OperationsResult upload(Operations<E> operations) {
                        return perform(operations);
                    }
                });
    }
}
