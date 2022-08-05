package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.device.Platform;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.util.Fetcher;
import com.foros.rs.client.util.FetcherImplementer;
import com.foros.rs.client.util.PagingAccessor;
import com.foros.rs.client.util.UrlBuilder;

public class PlatformService implements FetcherBuilder<PagingSelector, Platform> {

    private RsClient rsClient;

    public PlatformService(RsClient rsClient) {
        this.rsClient = rsClient;
    }

    private PagingAccessor<PagingSelector> pagingAccessor() {
        return new PagingAccessor<PagingSelector>() {
            @Override
            public PagingSelector getPaging(PagingSelector selector) {
                return selector;
            }

            @Override
            public PagingSelector setPaging(PagingSelector target, PagingSelector source) {
                return source;
            }
        };
    }

    public Result<Platform> get(PagingSelector selector) {
        String uri = UrlBuilder.path("/platforms").addQueryEntity(selector).build();
        return rsClient.get(uri);
    }

    @Override
    public Fetcher<PagingSelector, Platform> fetcher() {
        return new Fetcher<>(pagingAccessor(), new FetcherImplementer<PagingSelector, Platform>() {
            @Override
            public Result<Platform> fetch(PagingSelector selector) {
                return get(selector);
            }
        });
    }
}
