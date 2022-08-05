package com.foros.action.campaign.campaignCredit;

import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditPurpose;
import com.foros.restriction.annotation.Restrict;

public class EditCampaignCreditAction extends EditSaveCampaignCreditActionBase {
    private Long id;
    private Long accountId;

    @ReadOnly
    @Restrict(restriction="CampaignCredit.edit", parameters="find('Account',#target.accountId)")
    public String create() {
        campaignCredit = createEmptyCampaignCredit();
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="CampaignCredit.edit", parameters="find('CampaignCredit',#target.id)")
    public String edit() {
        campaignCredit = campaignCreditService.view(id);
        return SUCCESS;
    }

    private CampaignCredit createEmptyCampaignCredit() {
        CampaignCredit result = new CampaignCredit();
        AdvertisingAccountBase account = (AdvertisingAccountBase) accountService.find(accountId);
        result.setAccount(account);
        return result;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }
}