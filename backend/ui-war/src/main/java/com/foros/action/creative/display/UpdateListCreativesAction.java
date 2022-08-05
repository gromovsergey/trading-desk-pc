package com.foros.action.creative.display;

import com.foros.action.BaseActionSupport;
import com.foros.framework.Trim;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.session.account.AccountService;
import com.foros.session.creative.CreativeService;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

@Trim(include = "searchParams.campaignId", exclude="searchParams.sizeId")
public class UpdateListCreativesAction extends BaseActionSupport implements RequestContextsAware, AdvertiserSelfIdAware {
    private AdvertiserAccount account;

    @EJB
    private AccountService accountService;

    private Long advertiserId;

    private String changeType;

    @EJB
    private CreativeService creativeService;

    private String declineReason;

    private CreativeSearchForm searchParams = new CreativeSearchForm();

    private List<Long> setNumberIds = new ArrayList<Long>();

    public AdvertiserAccount getAccount() {
        if (account == null) {
            account = accountService.findAdvertiserAccount(advertiserId);
        }
        return account;
    }

    public String getChangeType() {
        return changeType;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public CreativeSearchForm getSearchParams() {
        return searchParams;
    }

    public List<Long> getSetNumberIds() {
        return setNumberIds;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public void setSearchParams(CreativeSearchForm searchParams) {
        this.searchParams = searchParams;
    }

    public void setTextCreativeIds(List<Long> ids) {
        this.setNumberIds.addAll(ids);
    }

    public void setDisplayCreativeIds(List<Long> ids) {
        this.setNumberIds.addAll(ids);
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(advertiserId);
    }

    public String update() {
        creativeService.bulkUpdateStatus(getSetNumberIds(), changeType, declineReason);
        return SUCCESS;
    }
}
