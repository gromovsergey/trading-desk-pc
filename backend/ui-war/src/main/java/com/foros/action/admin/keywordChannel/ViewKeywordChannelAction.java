package com.foros.action.admin.keywordChannel;

import com.foros.action.channel.ViewChannelActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.KeywordChannel;
import com.foros.session.channel.service.KeywordChannelService;

import javax.ejb.EJB;

public class ViewKeywordChannelAction extends ViewChannelActionSupport<KeywordChannel> implements BreadcrumbsSupport {

    @EJB
    private KeywordChannelService keywordChannelService;

    @ReadOnly
    public String view() throws Exception {
        loadChannel();
        loadCategories();
        return SUCCESS;
    }

    @Override
    protected KeywordChannel findChannel(Long id) {
        return keywordChannelService.view(id);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new KeywordChannelsBreadcrumbsElement()).add(new KeywordChannelBreadcrumbsElement(getModel()));
    }
}
