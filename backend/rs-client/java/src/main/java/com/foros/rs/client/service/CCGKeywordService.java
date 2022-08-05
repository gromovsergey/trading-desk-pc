package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.campaign.CCGKeyword;
import com.foros.rs.client.model.advertising.campaign.CCGKeywordSelector;

public class CCGKeywordService extends EntityServiceSupport<CCGKeyword, CCGKeywordSelector> {

    public CCGKeywordService(RsClient rsClient) {
        super(rsClient, "/keywords");
    }
}