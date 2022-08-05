package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.template.CreativeSize;
import com.foros.rs.client.model.advertising.template.CreativeSizeSelector;
import com.foros.rs.client.util.UrlBuilder;

public class CreativeSizeService  {
    private final String path = "/creativeSize";

    private final RsClient rsClient;

    public CreativeSizeService(RsClient rsClient) {
        this.rsClient = rsClient;
    }

    public CreativeSize get(CreativeSizeSelector selector) {
        String uri = UrlBuilder.path(path)
                .addQueryEntity(selector)
                .build();

        return rsClient.get(uri);
    }
}
