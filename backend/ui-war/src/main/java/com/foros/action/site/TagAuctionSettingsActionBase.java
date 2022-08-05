package com.foros.action.site;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.site.Tag;
import com.foros.model.site.TagAuctionSettings;
import com.foros.session.auctionSettings.AuctionSettingsService;
import com.foros.session.site.TagsService;
import com.foros.util.context.ContextBase;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class TagAuctionSettingsActionBase extends BaseActionSupport
        implements ModelDriven<TagAuctionSettings>, RequestContextsAware, BreadcrumbsSupport {

    @EJB
    protected TagsService tagsService;

    @EJB
    protected AuctionSettingsService auctionSettingsService;

    private Long id;
    private Tag tag;
    protected TagAuctionSettings auctionSettings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    protected Tag getTag() {
        if (tag == null) {
            tag = tagsService.view(id);
        }
        return tag;
    }

    @Override
    public TagAuctionSettings getModel() {
        return auctionSettings;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        ContextBase context = contexts.getPublisherContext();

        if (context != null) {
            context.switchTo(getTag().getAccount().getId());
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new SiteBreadcrumbsElement(getTag().getSite()))
                .add(new TagBreadcrumbsElement(getTag()))
                .add(new SimpleTextBreadcrumbsElement("AuctionSettings.breadcrumbs"));
    }
}
