package com.foros.action.admin;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.account.ExternalAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

public class ContactsAction extends BaseActionSupport {
    private User manager = new User();
    private User contact = new User();
    @EJB
    private AccountService service;

    @ReadOnly
    public String view() {
        Long accountId = SecurityContext.getPrincipal().getAccountId();

        ExternalAccount account = (ExternalAccount) service.find(accountId);

        if (account.getAccountManager() != null) {
            manager = account.getAccountManager();
        }

        InternalAccount intAccount = account.getInternalAccount();

        if (intAccount != null) {
            User lContact = null;
            switch (SecurityContext.getAccountRole()) {
                case ADVERTISER:
                case AGENCY:
                    lContact = intAccount.getAdvContact();
                    break;
                case PUBLISHER:
                    lContact = intAccount.getPubContact();
                    break;
                case ISP:
                    lContact = intAccount.getIspContact();
                    break;
                case CMP:
                    lContact = intAccount.getCmpContact();
                    break;
            }
            this.contact = lContact;
        }
        return SUCCESS;
    }

    public User getManager() {
        return manager;
    }

    public User getContact() {
        return contact;
    }
}
