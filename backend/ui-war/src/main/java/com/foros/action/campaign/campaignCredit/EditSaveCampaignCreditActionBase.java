package com.foros.action.campaign.campaignCredit;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CampaignCredit;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.campaignCredit.CampaignCreditService;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public abstract class EditSaveCampaignCreditActionBase extends BaseActionSupport implements ModelDriven<CampaignCredit>, RequestContextsAware {
    @EJB
    protected CampaignCreditService campaignCreditService;

    @EJB
    protected AccountService accountService;

    protected CampaignCredit campaignCredit;

    private Boolean allowSelectAdvertiser;
    private Collection<EntityTO> advertisers;
    private Account existingAccount;

    public Boolean isAllowSelectAdvertiser() {
        if (allowSelectAdvertiser == null) {
            AdvertisingAccountBase account = (AdvertisingAccountBase) accountService.find(campaignCredit.getAccount().getId());
            allowSelectAdvertiser = AccountRole.AGENCY.equals(account.getRole());
        }

        return allowSelectAdvertiser;
    }

    public Collection<EntityTO> getAdvertisers() {
        if (advertisers == null) {
            if (campaignCredit.getId() == null) {
                advertisers = accountService.findAdvertisersTOByAgency(campaignCredit.getAccount().getId());
            } else {
                List<Long> allocationsAdvertiserIds = campaignCreditService.getAllocationsAdvertiserIds(campaignCredit.getId());
                if (allocationsAdvertiserIds.isEmpty()) {
                    advertisers = accountService.findAdvertisersTOByAgency(campaignCredit.getAccount().getId());
                } else if (allocationsAdvertiserIds.size() == 1) {
                    Long advertiserId = allocationsAdvertiserIds.get(0);
                    AdvertiserAccount advertiser = accountService.findAdvertiserAccount(advertiserId);
                    advertisers = Arrays.asList(new EntityTO(advertiser.getId(), advertiser.getName(), advertiser.getStatus()));
                } else {
                    advertisers = Collections.emptyList();
                }
            }

            Long currentId = campaignCredit.getAdvertiser() != null ? campaignCredit.getAdvertiser().getId() : null;
            EntityUtils.applyStatusRules(advertisers, currentId, false);
        }

        return advertisers;
    }

    public Account getExistingAccount() {
        if (existingAccount != null) {
            return existingAccount;
        }

        existingAccount = accountService.find(campaignCredit.getAccount().getId());

        return existingAccount;
    }

    @Override
    public CampaignCredit getModel() {
        return campaignCredit;
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getAdvertiserContext().switchTo(campaignCredit.getAccount().getId());
    }
}