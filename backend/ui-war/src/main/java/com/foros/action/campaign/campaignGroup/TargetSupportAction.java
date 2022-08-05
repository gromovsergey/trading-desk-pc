package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.action.channel.SearchCriteria;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.channel.GenericChannel;
import com.foros.security.AccountRole;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class TargetSupportAction extends BaseActionSupport implements RequestContextsAware, ModelDriven<CampaignCreativeGroup>, BreadcrumbsSupport {

    @EJB
    protected CampaignCreativeGroupService groupService; 

    @EJB
    protected AccountService accountService;

    protected CampaignCreativeGroup group;
    
    protected AdvertisingAccountBase existingAccount;
    
    protected SearchCriteria searchCriteria = new SearchCriteria();

    public TargetSupportAction() {
      group = new CampaignCreativeGroup();
      group.setChannel(new GenericChannel());
    }

    public boolean isUsedAvailable() {
        return getExistingAccount().getRole() == AccountRole.AGENCY || getExistingAccount().getRole() == AccountRole.ADVERTISER;
    }

    public void setGroup(CampaignCreativeGroup group) {
        this.group = group;
    }

    public CampaignCreativeGroup getGroup() {
        return group;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getExistingAccount());
    }
    
    public char getChannelTargetLetter() {
        return group.getChannelTarget() != null ? group.getChannelTarget().getLetter() : '\u0000';
    }
    
    public void setChannelTargetLetter(char value) {
        group.setChannelTarget(ChannelTarget.valueOf(value));
    }

    public AdvertisingAccountBase getExistingAccount() {
        if (existingAccount == null) {
            existingAccount = (AdvertisingAccountBase)accountService.find(group.getAccount().getId());
        }
        return existingAccount;
    }

    @Override
    public CampaignCreativeGroup getModel() {
        return group;
    }

    public void setSearchCriteria(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new CampaignBreadcrumbsElement(group.getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(group))
                .add("channel.target.edit");
    }
}
