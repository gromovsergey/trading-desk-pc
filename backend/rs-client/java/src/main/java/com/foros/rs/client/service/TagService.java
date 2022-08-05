package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.publishing.Tag;
import com.foros.rs.client.model.publishing.TagEffectiveSizes;
import com.foros.rs.client.model.publishing.TagSelector;
import com.foros.rs.client.util.UrlBuilder;

public class TagService extends ReadonlyServiceSupport<TagSelector, Tag> {

    public TagService(RsClient rsClient) {
        super(rsClient, "/tags");
    }

    public TagEffectiveSizes effectiveSizes(Long tagId) {
        String uri = UrlBuilder.path(path + "/effectiveSizes").setQueryParameter("tag.id", tagId).build();
        return rsClient.get(uri);
    }
}
