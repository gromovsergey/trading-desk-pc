package com.foros.action.admin.keywordChannel;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.channel.KeywordChannel;

public class KeywordChannelBreadcrumbsElement extends EntityBreadcrumbsElement {
    public KeywordChannelBreadcrumbsElement(KeywordChannel keywordChannel) {
        super("channel.breadcrumbs.keywordChannel", keywordChannel.getId(), keywordChannel.getName(), "KeywordChannel/view");
    }
}
