package com.foros.action.campaign.campaignCredit;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.campaignCredit.CampaignCreditService;

import javax.ejb.EJB;

public class DeleteCampaignCreditAction extends BaseActionSupport {
    @EJB
    private CampaignCreditService campaignCreditService;

    private long id;
    private long accountId;

    public String delete() {
        campaignCreditService.delete(id);
        return SUCCESS;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}