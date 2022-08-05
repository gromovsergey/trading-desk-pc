package com.foros.util;

import com.foros.cache.application.CountryCO;
import com.foros.session.ServiceLocator;
import com.foros.session.admin.country.CountryService;
import com.foros.util.comparator.CountryCOComparator;
import com.foros.util.messages.MessageProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class CountryHelper {

    private CountryHelper() {}

    /**
     * Create and return comparator for countreis(CO) for ordering by sortOrder, name
     *
     * @return comparator
     */
    private static Comparator<CountryCO> createCountryComparator(MessageProvider provider) {
        return new CountryCOComparator(getResolver(provider));
    }

    private static FormatResolver getResolver(MessageProvider provider) {
        return new FormatResolver(provider, "global.country.{0}.name");
    }

    /**
     * Create duplicate of collection of countries and sort it
     *
     * @param countries collection of countries
     * @return sorted list of countries
     */
    public static List<CountryCO> sort(Collection<CountryCO> countries) {
        List<CountryCO> result = new ArrayList<CountryCO>(countries);
        Collections.sort(result, createCountryComparator(MessageProvider.createMessageProviderAdapter()));
        return result;
    }

    private static String resolveCountryName(MessageProvider provider, String countryCode) {
        return getResolver(provider).resolve(countryCode);
    }

    public static String resolveCountryName(String countryCode) {
        return resolveCountryName(MessageProvider.createMessageProviderAdapter(), countryCode);
    }

    public static LinkedHashMap<String, String> populateCountries() {
        LinkedHashMap<String, String> countries = new LinkedHashMap<String, String>();

        CountryService countryService = ServiceLocator.getInstance().lookup(CountryService.class);
        for (CountryCO countryCO : CountryHelper.sort(countryService.getIndex())) {
            String currentCountryCode = countryCO.getId();
            countries.put(currentCountryCode, StringUtil.resolveGlobal("country", currentCountryCode, false));
        }

        return countries;
    }
}
