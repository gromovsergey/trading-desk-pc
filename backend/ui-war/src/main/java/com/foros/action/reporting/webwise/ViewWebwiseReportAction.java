package com.foros.action.reporting.webwise;

import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.util.helper.IndexHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.IspSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.IspAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.util.CountryHelper;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;
import java.util.*;

public class ViewWebwiseReportAction extends BaseActionSupport implements RequestContextsAware, IspSelfIdAware {

    @EJB
    private AccountService accountService;

    private Long accountId;

    private Collection<IdNameBean> countries;

    private Collection<EntityTO> colocations;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'webwise'")
    public String view() {
        return SUCCESS;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Collection<IdNameBean> getCountries() {
        if (countries == null) {
            if (accountId == null) {
                Map<String, String> map = CountryHelper.populateCountries();
                countries = new ArrayList<IdNameBean>(map.keySet().size());
                for (String key : map.keySet()) {
                    countries.add(new IdNameBean(key, map.get(key)));
                }
            } else {
                countries = Collections.emptyList();
            }
        }
        return countries;
    }

    public Collection<EntityTO> getColocations() {
        if (colocations == null) {
            if (accountId != null) {
                colocations = IndexHelper.getColocationsList(accountId);
                EntityUtils.applyStatusRules(colocations, null, true);
            } else {
                colocations = Collections.emptyList();
            }
        }
        return colocations;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (accountId != null) {
            IspAccount ispAccount = accountService.findIspAccount(accountId);
            contexts.switchTo(ispAccount);
        }
    }

    @Override
    public void setIspId(Long ispId) {
        accountId = ispId;
    }
}
