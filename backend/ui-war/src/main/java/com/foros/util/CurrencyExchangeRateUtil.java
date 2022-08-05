package com.foros.util;

import com.foros.model.account.Account;
import com.foros.model.currency.Currency;
import com.foros.security.principal.SecurityContext;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;

public class CurrencyExchangeRateUtil {
    public static void populateCurrencyExchangeRate(HttpServletRequest request, Account account) {
        request.setAttribute("currencyExchangeRate", getCurrencyExchangeRate(account));
    }

    public static CurrencyConverter getCurrencyExchangeRate(Account account) {
        if (!SecurityContext.isInternal()) {
            AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
            account = accountService.getMyAccount();
        }

        Currency currency = account.getCurrency();
        return getCurrencyExchangeRate(currency.getId());
    }

    public static CurrencyConverter getCurrencyExchangeRate(Long currencyId) {
        CurrencyExchangeService exchangeService = ServiceLocator.getInstance().lookup(CurrencyExchangeService.class);
        return exchangeService.getCrossRate(currencyId, new Date());
    }
}
