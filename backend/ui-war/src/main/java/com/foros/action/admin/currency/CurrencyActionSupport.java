package com.foros.action.admin.currency;

import com.foros.action.BaseActionSupport;
import com.foros.cache.NamedCO;
import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencySource;
import com.foros.model.currency.Source;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.util.CurrencyHelper;
import com.foros.util.Resolver;
import com.foros.util.messages.MessageProvider;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

public class CurrencyActionSupport extends BaseActionSupport {
    @EJB
    protected CurrencyService currencyService;

    protected boolean valueDoesntExistInFeed = false;

    private Resolver resolver;

    private List<String> currencyCodesWithoutDefault;
    private List<Source> sourceValues = new ArrayList<Source>();

    public CurrencyActionSupport() {
        MessageProvider provider = MessageProvider.createMessageProviderAdapter();
        resolver = CurrencyHelper.getResolver(provider);
        sourceValues.add(Source.MANUAL);
        sourceValues.add(Source.FEED);
    }

    public List<Source> getSourceValues() {
        return sourceValues;
    }

    public boolean hasCurrenciesForCreate() {
        return !getAvailableCurrencyCodesWithoutDefault().isEmpty();
    }

    public List<String> getAvailableCurrencyCodesWithoutDefault() {
        if (currencyCodesWithoutDefault == null) {
            Currency defaultCurrency = currencyService.getDefault();
            currencyCodesWithoutDefault = new ArrayList<String>(CurrencySource.getCurrencyCodes());
            currencyCodesWithoutDefault.remove(defaultCurrency.getCurrencyCode());

            for (NamedCO<Long> currency : currencyService.getIndex()) {
                currencyCodesWithoutDefault.remove(currency.getName());
            }
        }

        return currencyCodesWithoutDefault;
    }
    

    public String getCurrencyName(String currencyCode) {
        String currencyName = resolver.resolve(currencyCode);
        if (currencyName == null) {
            currencyName = currencyCode;
        }
        return currencyName;
    }
    
    public String getCurrencySymbol(String currencyCode) {
        return CurrencyHelper.getCurrencySymbol(currencyCode);
    }

    public boolean isSourceEditable() {
        return currencyService.isSourceEditable();
    }

    public boolean isValueDoesntExistInFeed() {
        return valueDoesntExistInFeed;
    }
}
