package com.foros.action.account;

import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.InternalAccount;
import com.foros.session.EntityTO;
import com.foros.util.EntityUtils;

import java.util.Collections;
import java.util.List;

public abstract class EditSaveInternalAccountActionBase extends EditSaveAccountActionBase<InternalAccount> implements BreadcrumbsSupport {
    private List<EntityTO> allUsers;
    private List<EntityTO> advContactUsers;
    private List<EntityTO> pubContactUsers;
    private List<EntityTO> ispContactUsers;
    private List<EntityTO> cmpContactUsers;

    public List<EntityTO> getAdvContactUsers() {
        if (advContactUsers != null) {
            return advContactUsers;
        }

        Long accountId = account.getAdvContact() == null ? null : account.getAdvContact().getId();
        advContactUsers = EntityUtils.copyWithStatusRules(getAllUsers(), accountId, false);
        
        return advContactUsers;
    }

    public List<EntityTO> getPubContactUsers() {
        if (pubContactUsers != null) {
            return pubContactUsers;
        }

        Long accountId = account.getPubContact() == null ? null : account.getPubContact().getId();
        pubContactUsers = EntityUtils.copyWithStatusRules(getAllUsers(), accountId, false);

        return pubContactUsers;
    }

    public List<EntityTO> getIspContactUsers() {
        if (ispContactUsers != null) {
            return ispContactUsers;
        }

        Long accountId = account.getIspContact() == null ? null : account.getIspContact().getId();
        ispContactUsers = EntityUtils.copyWithStatusRules(getAllUsers(), accountId, false);

        return ispContactUsers;
    }

    public List<EntityTO> getCmpContactUsers() {
        if (cmpContactUsers != null) {
            return cmpContactUsers;
        }

        Long accountId = account.getCmpContact() == null ? null : account.getCmpContact().getId();
        cmpContactUsers = EntityUtils.copyWithStatusRules(getAllUsers(), accountId, false);

        return cmpContactUsers;
    }

    private List<EntityTO> getAllUsers() {
        if (allUsers != null) {
            return allUsers;
        }

        if (account.getId() != null) {
            allUsers = accountService.getAccountUsers(account.getId());
        } else {
            allUsers = Collections.emptyList();
        }

        return allUsers;
    }
}
