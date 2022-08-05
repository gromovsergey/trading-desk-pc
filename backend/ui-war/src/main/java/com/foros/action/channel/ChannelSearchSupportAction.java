package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.account.GenericAccount;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.AccountUtil;
import com.foros.util.CurrencyExchangeRateUtil;

import javax.ejb.EJB;

public class ChannelSearchSupportAction extends BaseActionSupport{

    @EJB
    protected SearchChannelService channelService; 

    protected Account account = new GenericAccount();

    protected Country country = new Country();

    private CurrencyConverter currencyExchangeRate;

    protected void prepare() {
        account = AccountUtil.extractAccount(account.getId());
        currencyExchangeRate = CurrencyExchangeRateUtil.getCurrencyExchangeRate(account);
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public void setCurrencyExchangeRate(CurrencyConverter currencyExchangeRate) {
        this.currencyExchangeRate = currencyExchangeRate;
    }

    public CurrencyConverter getCurrencyExchangeRate() {
        return currencyExchangeRate;
    }
}
