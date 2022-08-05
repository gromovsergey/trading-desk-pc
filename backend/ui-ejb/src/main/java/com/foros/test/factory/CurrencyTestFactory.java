package com.foros.test.factory;

import com.foros.model.currency.Currency;
import com.foros.session.admin.currency.CurrencyService;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class CurrencyTestFactory extends TestFactory<Currency> {
    @EJB
    private CurrencyService currencyService;

    public void populate(Currency currency) {
        currency.setEffectiveDate(new Timestamp(System.currentTimeMillis()));
        currency.setFractionDigits(2);
    }

    @Override
    public Currency create() {
        Currency currency = new Currency();
        return currency;
    }

    public Currency create(String currencyCode) {
        Currency currency = create();
        currency.setCurrencyCode(currencyCode);
        return currency;
    }

    @Override
    public void persist(Currency currency) {
        currencyService.create(currency);
    }

    public void update(Currency currency) {
        currencyService.update(currency);
    }

    @Override
    public Currency createPersistent() {
        Currency currency = create();
        persist(currency);
        return currency;
    }

    public Currency createPersistent(String currencyCode) {
        Currency currency = create(currencyCode);
        persist(currency);
        return currency;
    }

    public Currency findOrCreatePersistent(String currencyCode) {
        Currency currency;
        try {
            currency = findAny(Currency.class, new QueryParam("currencyCode", currencyCode));
        } catch (Exception e) {
            currency = createPersistent(currencyCode);
        }
        entityManager.flush();
        return currency;
    }
}
