package com.foros.action.xml.options;

import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.campaign.CampaignService;

import java.util.Collection;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class CampaignsXmlAction extends AbstractOptionsByAccountAction<EntityTO> {
    @EJB
    private CampaignService campaignService;

    private String accountPair;

    public CampaignsXmlAction() {
        super(new NamedTOConverter(true), new OptionStatusFilter(true));
    }

    @AccountId
    @RequiredStringValidator(key = "errors.required", message = "value.accountPair")
    @CustomValidator(type = "pair", key = "errors.pair", message = "value.accountPair")
    public String getAccountPair() {
        return accountPair;
    }

    public void setAccountPair(String accountPair) {
        this.accountPair = accountPair;
    }

    public CampaignService getCampaignService() {
        return campaignService;
    }

    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return getCampaignService().getCampaignsByAccount(accountId);
    }
}
