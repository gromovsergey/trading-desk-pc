package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.security.AccountRole;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.security.AccountTO;

import java.util.Collection;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class WalledGardenFreeAccountsXmlAction extends AccountsXmlAction {

    private String countryCode;
    @EJB
    private WalledGardenService walledGardenService;

    @Override
    protected Collection<? extends AccountTO> getOptions() throws ProcessException {
        AccountRole[] roles = getAccountRoles();
        switch (roles[0]) {
        case AGENCY:
            return getWalledGardenService().findFreeAgencyAccounts(getCountryCode());
        case PUBLISHER:
            return getWalledGardenService().findFreePublisherAccounts(getCountryCode());
        default:
            throw new IllegalArgumentException("Account role should be agency or publisher");
        }
    }
    
    @RequiredStringValidator(key = "errors.required", message = "countryCode")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public WalledGardenService getWalledGardenService() {
        return walledGardenService;
    }
}
