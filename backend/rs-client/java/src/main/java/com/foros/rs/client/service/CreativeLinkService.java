package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.campaign.CreativeLink;
import com.foros.rs.client.model.advertising.campaign.CreativeLinkSelector;

public class CreativeLinkService extends EntityServiceSupport<CreativeLink, CreativeLinkSelector> {

    public CreativeLinkService(RsClient rsClient) {
        super(rsClient, "/creativeLinks");
    }
}