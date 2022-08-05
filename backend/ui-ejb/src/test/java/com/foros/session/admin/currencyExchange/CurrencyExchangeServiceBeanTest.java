package com.foros.session.admin.currencyExchange;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencyExchange;
import com.foros.model.currency.CurrencyExchangeRate;
import com.foros.model.currency.CurrencyExchangeRatePK;
import com.foros.session.admin.CurrencyConverter;
import com.foros.test.factory.CurrencyTestFactory;

import com.foros.test.factory.CurrencyTestFactory;
import group.Db;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class CurrencyExchangeServiceBeanTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Autowired
    private CurrencyTestFactory currencyTF;

    @Test
    public void testUpdate() throws Exception {
        CurrencyExchange last = currencyExchangeService.findLast();
        assertNotNull(last);
        Collection<CurrencyExchangeRate> rates = new ArrayList<>(last.getCurrencyExchangeRates().size());

        for (CurrencyExchangeRate exchangeRate : last.getCurrencyExchangeRates()) {
            CurrencyExchangeRate newRate = new CurrencyExchangeRate();
            newRate.setId(new CurrencyExchangeRatePK(exchangeRate.getCurrency(), null));

            BigDecimal newRateVal = exchangeRate.getRate().add(BigDecimal.ONE);
            if (newRateVal.precision() - newRateVal.scale() > 7 || newRateVal.scale() > 5) {
                newRate.setRate(exchangeRate.getRate().subtract(BigDecimal.ONE));
            } else {
                newRate.setRate(exchangeRate.getRate().add(BigDecimal.ONE));
            }

            newRate.setLastUpdated(new Timestamp((new Date()).getTime()));

            rates.add(newRate);
        }

        currencyExchangeService.update(rates, last.getEffectiveDate());

        commitChanges();
        clearContext();

        CurrencyExchange newLast = currencyExchangeService.findLast();

        assertFalse(last.getId().equals(newLast.getId()));
        assertFalse(last.getEffectiveDate().equals(newLast.getEffectiveDate()));

        assertEquals(last.getCurrencyExchangeRates().size() , newLast.getCurrencyExchangeRates().size());
        for (CurrencyExchangeRate lastRate : rates) {
            CurrencyExchangeRate rate = find(newLast.getCurrencyExchangeRates(), lastRate.getCurrency());
            assertTrue(rate.getRate().compareTo(lastRate.getRate()) == 0);
        }
    }

    //@Test
    // A development test, do not include it in the regular unit testing
    public void checkCurrencyExistInFeed() throws IOException {
        // the returned map will have one item as passed currencies have the same hash code (id=null)
        currencyExchangeService.fetchFromFeed(Arrays.asList(
                currencyTF.create("RUB"),
                currencyTF.create("GBP"),
                currencyTF.create("JPY"),
                currencyTF.create("EUR")
        ));
    }

    private CurrencyExchangeRate find(Set<CurrencyExchangeRate> rates, Currency currency) {
        for (CurrencyExchangeRate rate : rates) {
            if (rate.getCurrency().equals(currency)) {
                return rate;
            }
        }
        throw new AssertionError("Rate not found" + currency);
    }

    @Test
    public void testGetCrossRate() {
        Currency currencyRUB = currencyTF.findOrCreatePersistent("RUB");
        Currency currencyGBP = currencyTF.findOrCreatePersistent("GBP");
        Currency currencyUSD = currencyTF.findOrCreatePersistent("USD");

        CurrencyConverter converter = currencyExchangeService.getCrossRate(currencyRUB.getId(), new Date());
        assertNotNull(converter);
        assertNotNull(converter.convert(currencyGBP.getId(), BigDecimal.ONE));
        assertNotNull(converter.convert(currencyUSD.getId(), BigDecimal.ONE));
    }
}
