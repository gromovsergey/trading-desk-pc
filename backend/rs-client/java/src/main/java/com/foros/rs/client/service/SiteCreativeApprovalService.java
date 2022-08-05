package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.data.JAXBEntity;
import com.foros.rs.client.model.siteCreative.SiteCreativeApproval;
import com.foros.rs.client.model.siteCreative.SiteCreativeApprovalOperations;
import com.foros.rs.client.model.siteCreative.SiteCreativeApprovalSelector;

public class SiteCreativeApprovalService extends ReadonlyServiceSupport<SiteCreativeApprovalSelector, SiteCreativeApproval> {

    public SiteCreativeApprovalService(RsClient rsClient) {
        super(rsClient, "/siteCreativeApprovals");
    }

    public void perform(SiteCreativeApprovalOperations operations) {
        rsClient.post(path, new JAXBEntity(operations));
    }
}