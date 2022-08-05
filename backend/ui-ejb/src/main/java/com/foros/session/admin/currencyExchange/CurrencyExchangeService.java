package com.foros.session.admin.currencyExchange;

import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencyExchange;
import com.foros.model.currency.CurrencyExchangeRate;
import com.foros.model.currency.Source;
import com.foros.session.admin.CurrencyConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface CurrencyExchangeService {

    void update(Collection<CurrencyExchangeRate> rates, Timestamp previousEffectiveDate);

    void updateFromFeed(Collection<CurrencyExchangeRate> rates);

    Map<Currency, BigDecimal> fetchFromFeed(Collection<Currency> currencies) throws IOException;

    Set<Currency> checkCurrencyExistInFeed(Collection<Currency> currencies);

    void switchExchangeUpdateTo(Source source);

    /**
     * security restricted action
     */
    CurrencyExchange viewLast();

    CurrencyExchange findLast();

    /**
     * Looks up last exchange rates for the currency  on a given date
     * @param currencyId id of the currency
     * @param date effective date of exchange rate
     * @return last exchange rate for the currency on a given date
     */
    CurrencyConverter getCrossRate(Long currencyId, Date date);

    CurrencyConverter getCrossRate(String currencyCode, Date date);

    void createCurrency(Currency currency);

    void updateCurrency(Currency currency);

    CurrencyExchange get(Long currencyExchangeId);
}
