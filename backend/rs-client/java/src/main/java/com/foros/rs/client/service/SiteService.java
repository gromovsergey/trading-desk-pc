package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.publishing.Site;
import com.foros.rs.client.model.publishing.SiteSelector;

public class SiteService extends ReadonlyServiceSupport<SiteSelector, Site> {

    public SiteService(RsClient rsClient) {
        super(rsClient, "/sites");
    }
}
