package com.foros.session.admin.currencyExchange;

import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencyExchangeRate;
import com.foros.model.currency.Source;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;

import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class CurrencyExchangeValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CurrencyService currencyService;

    @EJB
    private BeansValidationService beansValidationService;

    @Validation
    public void validateManual(ValidationContext context, Collection<CurrencyExchangeRate> rates, Timestamp previousEffectiveDate) {
        if (previousEffectiveDate == null) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("previousEffectiveDate");
        }

        validateUpdate(context, rates, Source.MANUAL);
    }

    @Validation
    public void validateFeed(ValidationContext context, Collection<CurrencyExchangeRate> rates) {
        validateUpdate(context, rates, Source.FEED);
    }

    private void validateUpdate(ValidationContext context, Collection<CurrencyExchangeRate> rates, Source source) {

        if (rates == null) {
            throw new NullPointerException("CurrencyExchangeRates is null");
        }

        Currency defaultCurrency = currencyService.getDefault();
        int i = 0;
        for (CurrencyExchangeRate rate : rates) {
            ValidationContext rateContext = context.subContext(rate)
                .withPath("currencyExchangeRates[" + i + "]")
                    .build();
            i++;

            Currency currency = em.find(Currency.class, rate.getCurrency().getId());

            if (currency.equals(defaultCurrency)) {
                rateContext.addConstraintViolation("errors.field.invalid")
                        .withPath("currency");
            }

            if (currency.getSource() != source) {
                rateContext.addConstraintViolation("errors.field.invalid")
                        .withPath("source");
            }

            beansValidationService.validate(rateContext);
        }
    }
}
