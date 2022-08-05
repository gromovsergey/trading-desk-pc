package com.foros.action.account;

import com.foros.action.BaseActionSupport;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.framework.ViewEditValidatable;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AccountSearchTestOption;
import com.foros.model.security.User;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.NamedTO;
import com.foros.session.account.AccountService;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.security.AccountStatsTO;
import com.foros.session.security.AccountTO;
import com.foros.session.security.UserService;
import com.foros.util.CountryHelper;
import com.foros.util.EntityUtils;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.context.ContextBase;
import com.foros.util.context.Contexts;
import com.foros.util.context.SessionContexts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

public abstract class SwitchContextActionBase<EntityT extends AccountStatsTO>
        extends BaseActionSupport implements ServletRequestAware, ViewEditValidatable {

    @EJB
    protected AccountService accountService;

    @EJB
    protected AccountTypeService accountTypeService;

    @EJB
    private UserService userService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CountryService countryService;
    protected HttpServletRequest request;

    private List<NamedTO> accountTypes;
    private List<AccountTO> internalAccounts;
    private List<CountryCO> countries;
    private List<EntityTO> accountManagers;
    private List<AccountSearchStatus> statuses;

    private List<EntityT> entities;
    protected Logger logger;

    private Long accountId;

    private String name;
    private Long accountTypeId;
    private Long internalAccountId;
    private String countryCode;
    private Long accountManagerId;
    private AccountSearchStatus  status = AccountSearchStatus.ALL_BUT_DELETED;
    private AccountSearchTestOption testOption = AccountSearchTestOption.EXCLUDE;

    public abstract String search();
    public abstract AccountRole getAccountRole();
    public abstract ContextBase getContext();
    public abstract ContextBase getSessionContext();

    protected Contexts getContexts() {
        return Contexts.getContexts(request);
    }
    protected Contexts getSessionContexts() {
        return SessionContexts.getSessionContexts(request);
    }

    public boolean isContextAvailable() {
        return getSessionContext().isSet();
    }

    @ReadOnly
    public String selectAccount() {
        /**
         *  To verify whether account exists for the given accountId.
         *  getAccountName() is called instead of find, as find has got security Policies
         *  and few userroles don't have view policies but needs to select an account
         *  to view sub-menus.
         *      e.g. INT_QA don't have VIEW_ACCOUNT privilege, but has privileges to access
         *  Sites, Creatives, Channels which are under Advertisers and Publishers Main menu.
         */
        accountService.getAccountName(accountId);

        getContext().switchTo(accountId);

        return successPath();
    }

    @ReadOnly
    public String switchContext() {
        getSessionContext().clear();
        return SUCCESS;
    }

    public String getAccountRoleName() {
        return getAccountRole().getName();
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }

    public List<EntityT> getEntities() {
        return this.entities;
    }

    public void setEntities(List<EntityT> entities) {
        this.entities = entities;
    }

    @ReadOnly
    @Restrict(restriction = "Context.switch", parameters = "#target.accountRole")
    public String main() {
        if (isContextAvailable()) {
            return successPath();
        } else {
            return INPUT;
        }
    }

    public String successPath() {
        return SUCCESS;
    }

    public List<EntityTO> getAccountManagers() {
        if(accountManagers==null){
            accountManagers = userService.getAccountManagers(internalAccountId, getAccountRole());
            Collections.sort(accountManagers, new IdNameComparator());

            addNoneOption();
            EntityUtils.applyStatusRules(accountManagers, null, true);
        }
        return accountManagers;
    }

    public List<NamedTO> getAccountTypes() {
        if (accountTypes == null) {
            populateAccountTypes();
        }
        return accountTypes;
    }

    public List<AccountTO> getInternalAccounts() {
        if (internalAccounts == null) {
            internalAccounts = accountService.search(AccountRole.INTERNAL);
            EntityUtils.applyStatusRules(internalAccounts, null);
        }
        return internalAccounts;
    }

    public List<CountryCO> getCountries() {
        if (countries == null) {
            countries = CountryHelper.sort(countryService.getIndex());
        }
        return countries;
    }

    public List<AccountSearchStatus> getStatuses() {
        if (statuses == null) {
            statuses = getSearchStatuses();
        }
        return statuses;
    }

    public AccountSearchTestOption[] getTestOptions() {
        return AccountSearchTestOption.values();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public Long getInternalAccountId() {
        return internalAccountId;
    }

    public void setInternalAccountId(Long internalAccountId) {
        this.internalAccountId = internalAccountId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getAccountManagerId() {
        return accountManagerId;
    }

    public void setAccountManagerId(Long accountManagerId) {
        this.accountManagerId = accountManagerId;
    }

    public AccountSearchStatus getStatus() {
        return status;
    }

    public void setStatus(AccountSearchStatus status) {
        this.status = status;
    }

    public AccountSearchTestOption getTestOption() {
        return testOption;
    }

    public void setTestOption(AccountSearchTestOption testOption) {
        this.testOption = testOption;
    }

    public Long getAccountId() {
        if (accountId == null) {
            return getSessionContext().getAccountId();
        }

        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    protected void populateAccountTypes() {
        accountTypes = accountTypeService.findIndexByRole(getAccountRole().getName());
    }

    private void addNoneOption() {
        accountManagers.add(0, new EntityTO(0L, getText("form.select.none"), Status.ACTIVE.getLetter()));
    }

    @Override
    public boolean viewValidate() {
        if (getAccountId() == null) {
            throw new EntityNotFoundException("Entity with id = " + getAccountId() + " not found");
        }
        
        return true;
    }

    @Override
    public boolean editValidate() {
        return true;
    }

    public String getMyCountry() {
        Account myAccount = accountService.getMyAccount();
        return myAccount.getCountry().getCountryCode();
    }

    public boolean isShowAccountManager() {
        User currentUser = userService.getMyUser();
        return !currentUser.getRole().isAccountManager();
    }

    public boolean isShowMyAccount() {
        if (currentUserService.isInternalWithRestrictedAccess()) {
            return currentUserService.getAccessAccountIds().contains(currentUserService.getAccountId());
        }
        return true;
    }

    private List<AccountSearchStatus> getSearchStatuses(){
        List<AccountSearchStatus> statuses = new ArrayList<AccountSearchStatus>();
        if (AccountRole.ADVERTISER.equals(getAccountRole())) {
            if (userService.getMyUser().isDeletedObjectsVisible()) {
                statuses.add(AccountSearchStatus.ALL);
                statuses.add(AccountSearchStatus.ALL_BUT_DELETED);
                statuses.add(AccountSearchStatus.LIVE);
                statuses.add(AccountSearchStatus.NOT_LIVE);
                statuses.add(AccountSearchStatus.INACTIVE);
                statuses.add(AccountSearchStatus.DELETED);
            } else {
                statuses.add(AccountSearchStatus.ALL_HIDE_DELETED);
                statuses.add(AccountSearchStatus.LIVE);
                statuses.add(AccountSearchStatus.NOT_LIVE);
                statuses.add(AccountSearchStatus.INACTIVE);
            }
        } else {
            if (userService.getMyUser().isDeletedObjectsVisible()) {
                statuses.add(AccountSearchStatus.ALL);
                statuses.add(AccountSearchStatus.ALL_BUT_DELETED);
                statuses.add(AccountSearchStatus.LIVE);
                statuses.add(AccountSearchStatus.INACTIVE);
                statuses.add(AccountSearchStatus.DELETED);
            } else {
                statuses.add(AccountSearchStatus.ALL_HIDE_DELETED);
                statuses.add(AccountSearchStatus.LIVE);
                statuses.add(AccountSearchStatus.INACTIVE);
            }
        }
        return statuses;
    }
}
