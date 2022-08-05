package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.data.JAXBEntity;
import com.foros.rs.client.model.siteCreative.ThirdPartyCreative;
import com.foros.rs.client.model.siteCreative.ThirdPartyCreativeOperations;
import com.foros.rs.client.model.siteCreative.ThirdPartyCreativeSelector;

public class ThirdPartyCreativeService extends ReadonlyServiceSupport<ThirdPartyCreativeSelector, ThirdPartyCreative> {

    public ThirdPartyCreativeService(RsClient rsClient) {
        super(rsClient, "/thirdPartyCreatives");
    }

    public void perform(ThirdPartyCreativeOperations operations) {
        rsClient.post(path, new JAXBEntity(operations));
    }
}