package com.foros.action.campaign;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.framework.support.TimeZoneAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.Campaign;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.util.EntityUtils;
import com.foros.util.StringUtil;
import com.foros.util.context.RequestContexts;

import java.util.Arrays;
import java.util.Date;
import javax.ejb.EJB;
import java.util.List;
import java.util.TimeZone;

public abstract class EditSaveCampaignActionBase extends CampaignActionSupport implements TimeZoneAware, RequestContextsAware, BreadcrumbsSupport {
    @EJB
    protected AccountService accountService;

    @EJB
    private WalledGardenService walledGardenService;

    private Long currentAdvertiserId; // for internal usage only. Used to prevent advertiserId change for external users
    protected AdvertiserAccount existingAccount;

    protected Boolean dateEndSet;
    protected DateTimeBean selectedDateStart = new DateTimeBean();
    protected DateTimeBean defaultDateEnd = new DateTimeBean();
    protected DateTimeBean selectedDateEnd = new DateTimeBean();

    protected char type;

    private List<EntityTO> availableUsers;
    private List<EntityTO> salesManagers;
    private List<EntityTO> soldToUsers;
    private List<EntityTO> billToUsers;

    private Boolean endDateCleanAllowed;
    private Boolean walledGardenEnabled;
    private CampaignBudgetType budgetType;
    protected Boolean editBudget = false;
    protected String excludedChannels;
    protected List<String> excludedChannelsList;

    protected Long getCurrentAdvertiserId() {
        if (currentAdvertiserId != null) {
            return currentAdvertiserId;
        }

        if (campaign.getId() == null) {
            currentAdvertiserId = getCurrentAdvertiserId(campaign.getAccount().getId());
        } else {
            currentAdvertiserId = campaignService.find(campaign.getId()).getAccount().getId();
        }

        return currentAdvertiserId;
    }

    public AdvertiserAccount getExistingAccount() {
        if (existingAccount != null) {
            return existingAccount;
        }

        existingAccount = accountService.findAdvertiserAccount(getCurrentAdvertiserId());

        return existingAccount;
    }

    public AdvertisingAccountBase getStandaloneAccount() {
        AdvertiserAccount account = getExistingAccount();
        return account.isStandalone() ? account : account.getAgency();
    }

    @Override
    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(getExistingAccount().getTimezone().getKey());
    }

    public Boolean isDateEndSet() {
        return dateEndSet;
    }

    public void setDateEndSet(Boolean dateEndSet) {
        this.dateEndSet = dateEndSet;
    }

    public DateTimeBean getSelectedDateStart() {
        return selectedDateStart;
    }

    public DateTimeBean getDefaultDateEnd() {
        defaultDateEnd.setDate(new Date(), getTimeZone(), CurrentUserSettingsHolder.getLocale());
        return defaultDateEnd;
    }

    public DateTimeBean getSelectedDateEnd() {
        return selectedDateEnd;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public List<EntityTO> getSalesManagers() {
        if (salesManagers != null) {
            return salesManagers;
        }

        salesManagers = accountService.getSalesManagers(getExistingAccount());

        return salesManagers;
    }

    public List<EntityTO> getSoldToUsers() {
        if (soldToUsers != null) {
            return soldToUsers;
        }

        soldToUsers = EntityUtils.copyWithStatusRules(getAvailableUsers(), campaign.getSoldToUser() != null ? campaign.getSoldToUser().getId() : null, false);

        return soldToUsers;
    }

    public List<EntityTO> getBillToUsers() {
        if (billToUsers != null) {
            return billToUsers;
        }

        billToUsers = EntityUtils.copyWithStatusRules(getAvailableUsers(), campaign.getBillToUser() != null ? campaign.getBillToUser().getId() : null, false);

        return billToUsers;
    }

    private List<EntityTO> getAvailableUsers() {
        if (availableUsers != null) {
            return availableUsers;
        }

        Long accountId;

        if (getExistingAccount().isInAgencyAdvertiser()) {
            accountId = getExistingAccount().getAgency().getId();
        } else {
            accountId = getExistingAccount().getId();
        }

        availableUsers = accountService.getAccountUsers(accountId);

        return availableUsers;
    }

    public boolean isEndDateCleanAllowed() {
        if (endDateCleanAllowed != null) {
            return endDateCleanAllowed;
        }

        endDateCleanAllowed = campaign.getId() == null || campaignService.isEndDateCleanAllowed(campaign.getId());

        return endDateCleanAllowed;
    }

    public boolean isWalledGardenEnabled() {
        if (walledGardenEnabled != null) {
            return walledGardenEnabled;
        }

        Long accountId;

        AdvertiserAccount account = getExistingAccount();

        if (account.isInAgencyAdvertiser()) {
            accountId = account.getAgency().getId();
            walledGardenEnabled = walledGardenService.isAgencyWalledGarden(accountId);
        } else {
            walledGardenEnabled = false;
        }

        return walledGardenEnabled;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getExistingAccount());
    }
    
    // TODO: AdvertiserSelfIdAware to be used.
    static Long getCurrentAdvertiserId(Long advertiserId) {
        Long currentAdvertiserId;
        if (advertiserId == null && !SecurityContext.isInternal()) {
            currentAdvertiserId = SecurityContext.getPrincipal().getAccountId();
        } else {
            currentAdvertiserId = advertiserId;
        }
        return currentAdvertiserId;
    }

    public CampaignBudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(CampaignBudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public abstract Campaign getBreadcrumbsEntity();

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (campaign.getId() != null) {
            Campaign entity = getBreadcrumbsEntity();
            breadcrumbs = new Breadcrumbs().add(new CampaignBreadcrumbsElement(entity)).add(ActionBreadcrumbs.EDIT);
        }
        return breadcrumbs;
    }

    public Boolean getEditBudget() {
        return editBudget;
    }

    public void setEditBudget(Boolean editBudget) {
        this.editBudget = editBudget;
    }

    public String getExcludedChannels() {
        return excludedChannels;
    }

    public void setExcludedChannels(String excludedChannels) {
        this.excludedChannels = excludedChannels;
    }

    public List<String> getExcludedChannelsList() {
        if (excludedChannelsList == null) {
            excludedChannelsList = Arrays.asList(StringUtil.splitAndTrim(excludedChannels));
        }
        return excludedChannelsList;
    }
}
