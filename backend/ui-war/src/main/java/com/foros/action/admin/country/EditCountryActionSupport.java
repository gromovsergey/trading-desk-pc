package com.foros.action.admin.country;

import com.foros.cache.NameComparator;
import com.foros.cache.NamedCO;
import com.foros.model.security.Language;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.util.CurrencyHelper;
import com.foros.util.MessageHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;

class EditCountryActionSupport extends CountryActionSupport {

    @EJB
    private CurrencyService currencyService;
    protected String id;

    public Collection<NamedCO<Long>> getAvailableCurrencies() {
        List<NamedCO<Long>> populatedCurrencies = new LinkedList<NamedCO<Long>>(currencyService.getIndex());
        Collections.sort(populatedCurrencies,
                CurrencyHelper.getCurrencyComparator());

        return populatedCurrencies;
    }

    public Collection<NamedCO<Long>> getAvailableTimeZones() {
        AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
        List<NamedCO<Long>> timezones = new ArrayList<NamedCO<Long>>(accountService.getTimeZoneIndex());

        for (NamedCO<Long> timezone : timezones) {
            String key = "global.timezone." + prepareTimeZoneName(timezone.getName()) + ".name";
            timezone.setName(getText(key));
        }

        Collections.sort(timezones, new NameComparator());
        return timezones;
    }

    public Collection<Language> getAvailableLanguages() {
        return Arrays.asList(Language.values());
    }

    private static String prepareTimeZoneName(String name) {
        return MessageHelper.prepareMessageKey(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
