package com.foros.action.security.auditLog;

import com.foros.action.IdNameForm;

public class CampaignCreativeGroupForm extends IdNameForm<String> {
    private IdNameForm<String> campaign = new IdNameForm<String>();
    private String ccgType;

    public IdNameForm<String> getCampaign() {
        return campaign;
    }

    public void setCampaign(IdNameForm<String> campaign) {
        this.campaign = campaign;
    }

    public String getCcgType() {
        return ccgType;
    }

    public void setCcgType(String ccgType) {
        this.ccgType = ccgType;
    }
}
