package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.template.CreativeTemplate;
import com.foros.rs.client.model.advertising.template.CreativeTemplateSelector;
import com.foros.rs.client.util.UrlBuilder;

public class CreativeTemplateService {

    private final RsClient rsClient;

    public CreativeTemplateService(RsClient rsClient) {
        this.rsClient = rsClient;
    }

    public CreativeTemplate get(CreativeTemplateSelector selector) {
        String uri = UrlBuilder.path("/creativeTemplate")
                .addQueryEntity(selector)
                .build();

        return rsClient.get(uri);
    }
}
