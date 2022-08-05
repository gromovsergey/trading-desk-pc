package com.foros.session.admin.currency;

import com.foros.AbstractValidationsTest;
import com.foros.model.currency.Currency;
import com.foros.model.currency.Source;
import com.foros.test.factory.CurrencyTestFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Validation.class })
public class CurrencyValidationsTest extends AbstractValidationsTest {
    @Autowired
    public CurrencyTestFactory currencyTestFactory;

    @Test
    public void testDefaultCreate() {
        Currency currency = new Currency();
        currency.setCurrencyCode("RUB");
        currency.setEffectiveDate(new Timestamp((new Date()).getTime()));
        currency.setFractionDigits(2);
        currency.setRate(BigDecimal.ONE);
        testDefaultInternal("Currency.create", currency);

        currency.setId(10L);
        validate("Currency.create", currency);
        assertHasViolation("id");
    }

    @Test
    public void testDefaultUpdate() {
        Currency currency = currencyTestFactory.findOrCreatePersistent("RUB");
        currency.setSource(Source.MANUAL);
        currency.setRate(null);
        validate("Currency.update", currency);
        assertFalse(violations.isEmpty());
        currency.setRate(new BigDecimal(1));
        testDefaultInternal("Currency.update", currency);

        currency.setVersion(new Timestamp((new Date()).getTime()));
        getEntityManager().clear();
        validate("Currency.update", currency);
        assertHasViolation("version");
    }

    private void testDefaultInternal(String validationName, Currency currency) {
        validate(validationName, currency);
        assertTrue(violations.isEmpty());

        currency.setRate(null);
        validate(validationName, currency);
        assertHasViolation("rate");

        currency.setRate(new BigDecimal("0"));
        validate(validationName, currency);
        assertHasViolation("rate");

        currency.setRate(new BigDecimal("10.2222222222"));
        validate(validationName, currency);
        assertHasViolation("rate");

        currency.setCurrencyCode("qaeqweqeqe");
        validate(validationName, currency);
        assertHasViolation("currencyCode");
    }
}

