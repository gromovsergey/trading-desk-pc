package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.campaign.CCGKeywordService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.util.context.RequestContexts;

import java.sql.Timestamp;
import javax.ejb.EJB;

public abstract class EditSaveKeywordsActionBase extends BaseActionSupport implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    protected CCGKeywordService ccgKeywordService;

    private CampaignCreativeGroup existingGroup;

    protected Long id;
    protected Timestamp ccgVersion;
    protected String keywordsText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeywordsText() {
        return keywordsText;
    }

    public void setKeywordsText(String keywordsText) {
        this.keywordsText = keywordsText;
    }

    public Timestamp getCcgVersion() {
        return ccgVersion;
    }

    public void setCcgVersion(Timestamp ccgVersion) {
        this.ccgVersion = ccgVersion;
    }

    public CampaignCreativeGroup getExistingGroup() {
        if (existingGroup != null) {
            return existingGroup;
        }

        existingGroup = campaignCreativeGroupService.find(id);

        return existingGroup;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getExistingGroup().getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new CampaignBreadcrumbsElement(getExistingGroup().getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(getExistingGroup()))
                .add("keywords.breadcrumbs.edit.list");
    }
}
