package com.foros.action.account;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.model.account.Account;
import com.foros.session.account.AccountService;
import com.foros.session.admin.country.CountryService;

import javax.ejb.EJB;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AccountActionBase<T extends Account> extends BaseActionSupport implements ModelDriven<T> {
    @EJB
    protected AccountService accountService;

    @EJB
    protected CountryService countryService;

    protected T account;

    private Map<String, Boolean> cmpShowPhoneValues = new LinkedHashMap<String, Boolean>();

    protected AccountActionBase() {
        cmpShowPhoneValues.put("cmpAccount.dontShowPhone", false);
        cmpShowPhoneValues.put("cmpAccount.showPhone", true);
    }

    @Override
    public T getModel() {
        return account;
    }

    public T getEntity() {
        return getModel();
    }

    public Map<String, Boolean> getCmpShowPhoneValues() {
        return cmpShowPhoneValues;
    }
}
