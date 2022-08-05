package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.conversion.ConversionAssociation;
import com.foros.rs.client.model.advertising.conversion.ConversionAssociationsSelector;
import com.foros.rs.client.util.UrlBuilder;

public class ConversionAssociationsService {

    private final RsClient rsClient;
    private final String path = "/conversions/associations";

    public ConversionAssociationsService(RsClient client) {
        rsClient = client;
    }

    public final ConversionAssociation get(ConversionAssociationsSelector selector) {
        String uri = UrlBuilder.path(path)
            .addQueryEntity(selector)
            .build();
        return rsClient.get(uri);
    }

}
