package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.data.JAXBEntity;
import com.foros.rs.client.model.advertising.campaign.Creative;
import com.foros.rs.client.model.advertising.campaign.CreativeSelector;
import com.foros.rs.client.model.advertising.template.LivePreviewResult;

public class CreativeService extends EntityServiceSupport<Creative, CreativeSelector> {

    public CreativeService(RsClient rsClient) {
        super(rsClient, "/creatives");
    }

    public LivePreviewResult preview(Long creativeId) {
        return rsClient.get("/creatives/preview?creative.id=" + creativeId);
    }

    public LivePreviewResult livePreview(Creative creative) {
        return rsClient.post("/creatives/livePreview", new JAXBEntity(creative));
    }
}