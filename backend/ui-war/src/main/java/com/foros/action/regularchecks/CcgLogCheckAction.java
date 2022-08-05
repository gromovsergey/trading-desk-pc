package com.foros.action.regularchecks;

import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.security.AccountType;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.regularchecks.RegularReviewService;

import java.util.SortedMap;

import javax.ejb.EJB;

public class CcgLogCheckAction extends LogChecksAction<CampaignCreativeGroup> {

    private CampaignCreativeGroup model = new CampaignCreativeGroup();

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private RegularReviewService regularReviewService;

    @ReadOnly
    public String edit() {
        model = campaignCreativeGroupService.find(getEntityId());
        return SUCCESS;
    }

    public String updateCCGCheck() throws Exception {
        regularReviewService.updateCCGCheck(getModel());
        return SUCCESS;
    }

    @Override
    public CampaignCreativeGroup getModel() {
        return model;
    }

    protected SortedMap<Integer, String> getAvailableIntervals(AccountType at, Integer lastCheckInterval) {
        return getAvailableIntervals(at.getCampaignFirstCheck(), at.getCampaignSecondCheck(), at.getCampaignThirdCheck(), lastCheckInterval);
    }

    public AdvertiserAccount getAccount() {
        return model.getAccount();
    }

    public void setAccount(AdvertiserAccount account) {
        if (model.getCampaign() == null) {
            model.setCampaign(new Campaign());
        }
        model.getCampaign().setAccount(account);
    }
}
