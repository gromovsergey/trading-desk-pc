package com.foros.action.reporting.siteChannels;

import com.foros.action.BaseActionSupport;
import com.foros.util.helper.IndexHelper;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.security.AccountTO;

import java.util.Collection;

public class ViewSiteChannelsReportingAction extends BaseActionSupport {
    private Collection<AccountTO> accounts;


    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'siteChannels'")
    public String view() throws Exception {
        return "success";
    }

    public Collection<AccountTO> getAccounts() {
        if (accounts == null) {
            accounts = IndexHelper.getAccountsList(AccountRole.PUBLISHER);
        }

        return accounts;
    }

}
