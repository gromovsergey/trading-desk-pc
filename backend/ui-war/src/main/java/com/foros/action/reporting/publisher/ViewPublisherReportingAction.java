package com.foros.action.reporting.publisher;

import com.foros.action.BaseActionSupport;
import com.foros.util.helper.IndexHelper;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.account.PublisherAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.NamedTO;
import com.foros.session.account.AccountService;
import com.foros.session.reporting.ReportingHelper;
import com.foros.session.reporting.publisher.DetailLevel;
import com.foros.session.reporting.publisher.PublisherMeta;
import com.foros.session.security.AccountTO;
import com.foros.util.CountryHelper;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewPublisherReportingAction extends BaseActionSupport implements RequestContextsAware {
    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;

    private Long accountId;

    private List<AccountTO> accounts;
    private List<EntityTO> sites;
    private List<CountryCO> countries;

    private NamedTO account;
    private DetailLevel detailLevel;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'publisher'")
    public String view() throws Exception {
        if (!isInternal()) {
            accountId = currentUserService.getAccountId();
        }

        if (accountId == null) {
            accounts = IndexHelper.getAccountsList(AccountRole.PUBLISHER);
            if (accounts != null && !accounts.isEmpty()) {
                sites = IndexHelper.getSitesList(accounts.iterator().next().getId());
            }
        } else {
            accounts = Collections.emptyList();
            sites = IndexHelper.getSitesList(accountId);
            PublisherAccount publisherAccount = accountService.findPublisherAccount(accountId);
            account = new NamedTO(publisherAccount.getId(), publisherAccount.getName());
        }

        countries = CountryHelper.sort(IndexHelper.getCountryList());
        detailLevel = DetailLevel.date;

        return "success";
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (accountId != null) {
            Account account = accountService.find(accountId);
            contexts.switchTo(account);
        }
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public List<AccountTO> getAccounts() {
        return accounts;
    }

    public List<EntityTO> getSites() {
        return sites;
    }

    public List<CountryCO> getCountries() {
        return countries;
    }

    public NamedTO getAccount() {
        return account;
    }

    public DetailLevel getDetailLevel() {
        return detailLevel;
    }

    public List<String> getColumnsSortPattern() {
        return ReportingHelper.getColumnsSortPattern(PublisherMeta.ALL);
    }

    public List<String> getMandatoryColumns() {
        List<String> mandatoryColumns = new ArrayList<String>();
        mandatoryColumns.addAll(ReportingHelper.getColumnNamesList(PublisherMeta.MANDATORY_WG_COLUMNS, true));
        mandatoryColumns.addAll(ReportingHelper.getColumnNamesList(PublisherMeta.MANDATORY_NON_WG_COLUMNS, true));
        return mandatoryColumns;
    }

    public boolean canSelectAccount() {
        return isInternal() && accountId == null;
    }

    public boolean isInternal() {
        return currentUserService.isInternal();
    }
}
