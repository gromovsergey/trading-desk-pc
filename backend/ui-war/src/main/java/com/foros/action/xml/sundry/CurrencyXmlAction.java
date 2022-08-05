package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.account.Account;
import com.foros.model.currency.Currency;
import com.foros.session.account.AccountService;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;

public class CurrencyXmlAction extends AbstractXmlAction<Currency> {
    @EJB
    private AccountService accountService;

    private String accountPair;

    @CustomValidator(type = "pair", key = "errors.pair", message = "accountPair")
    public String getAccountPair() {
        return accountPair;
    }

    public void setAccountPair(String accountPair) {
        this.accountPair = accountPair;
    }

    public Currency generateModel() throws ProcessException {
        Account account;
        if (StringUtil.isPropertyNotEmpty(getAccountPair())) {
            Long id = PairUtil.fetchId(getAccountPair());
            account = accountService.view(id);
        } else {
            account = accountService.getMyAccount();
        }
        return account.getCurrency();
    }

}
