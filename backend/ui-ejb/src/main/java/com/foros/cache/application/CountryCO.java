package com.foros.cache.application;

import com.foros.cache.CacheObject;
import com.foros.cache.NamedCO;
import com.foros.model.Country;

public class CountryCO extends CacheObject<String> {

    private Long sortOrder;
    private NamedCO<Long> currency = null;
    private NamedCO<Long> timezone = null;
    private Long countryId;

    public CountryCO(Country country) {
        this(country.getCountryCode(), country.getSortOrder(),
                (country.getCurrency() != null) ? country.getCurrency().getId() : null,
                (country.getCurrency() != null) ? country.getCurrency().getCurrencyCode() : null,
                (country.getTimezone() != null) ? country.getTimezone().getId() : null,
                (country.getTimezone() != null) ? country.getTimezone().getKey() : null,
                country.getCountryId());
    }

    public CountryCO(String countryCode, Long sortOrder, Long currencyId, String currencyCode, Long timezoneId, String timezoneKey, Long countryId) {
        super(countryCode);
        this.sortOrder = sortOrder;
        this.countryId = countryId;
        if (currencyId != null) {
            this.currency = new NamedCO<Long>(currencyId, currencyCode);
        }
        if (timezoneId != null) {
            this.timezone = new NamedCO<Long>(timezoneId, timezoneKey);
        }
    }

    public NamedCO<Long> getCurrency() {
        return currency;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public NamedCO<Long> getTimezone() {
        return timezone;
    }

    public Long getCountryId() {
        return countryId;
    }
}
