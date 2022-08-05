package com.foros.action.admin.currencyExchange;

import com.foros.action.BaseActionSupport;
import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencyExchange;
import com.foros.model.currency.CurrencyExchangeRate;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;
import com.foros.util.CurrencyHelper;
import com.foros.util.StringUtil;
import com.foros.util.messages.MessageProvider;

import javax.ejb.EJB;
import java.util.*;

public class CurrencyExchangeActionSupport extends BaseActionSupport {

    private CurrencyExchange exchange;

    private List<CurrencyBean> currencyExchangeRates;

    @EJB
    protected CurrencyExchangeService service;

    public List<CurrencyBean> getCurrencyExchangeRates() {
        if (currencyExchangeRates == null) {
            Set<CurrencyExchangeRate> rates = getExchange().getCurrencyExchangeRates();
            currencyExchangeRates = new LinkedList<CurrencyBean>();

            for (CurrencyExchangeRate rate : rates) {
                Currency currency = rate.getCurrency();
                String currencySymbol = CurrencyHelper.getCurrencySymbol(currency.getCurrencyCode());
                String currencyName = CurrencyHelper.resolveCurrencyName(
                        MessageProvider.createMessageProviderAdapter(), currency.getCurrencyCode());

                if (currencyName == null) {
                    currencyName = currency.getCurrencyCode();
                }
                CurrencyBean er = new CurrencyBean(currency.getId(), currencyName, currencySymbol);
                er.setRate(rate.getRate());
                er.setSource(currency.getSource());
                currencyExchangeRates.add(er);
            }

            Collections.sort(currencyExchangeRates, new Comparator<CurrencyBean>() {
                public int compare(CurrencyBean o1, CurrencyBean o2) {
                    return StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
                }
            });

        }
        return currencyExchangeRates;
    }

    public CurrencyExchange getExchange() {
        if (exchange == null) {
            exchange = service.viewLast();
        }
        return exchange;
    }
}
