package com.foros.rs.client.service;

import com.foros.rs.client.data.JAXBEntity;
import com.foros.rs.client.model.restriction.Predicates;
import com.foros.rs.client.model.restriction.RestrictionCommandsOperation;
import com.foros.rs.client.RsClient;
import com.foros.rs.client.util.UrlBuilder;


public class RestrictionService {
    private RsClient rsClient;

    public RestrictionService(RsClient rsClient) {
        this.rsClient = rsClient;
    }

    public Predicates get(RestrictionCommandsOperation commands) {
        String uri = UrlBuilder.path("/restriction")
                .build();
        return rsClient.post(uri, new JAXBEntity(commands));
    }
}
