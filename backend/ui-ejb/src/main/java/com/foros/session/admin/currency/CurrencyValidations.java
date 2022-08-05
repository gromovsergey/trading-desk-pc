package com.foros.session.admin.currency;

import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencySource;
import com.foros.model.currency.Source;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.strategy.ValidationMode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Stateless
@Validations
public class CurrencyValidations {
    
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CurrencyExchangeService currencyExchangeService;

    @EJB
    private BeansValidationService beanValidationService;
    
    @Validation
    public void validateCreate(ValidationContext validationContext, Currency currency) {
        ValidationContext context = validationContext
                .subContext(currency)
                .withMode(ValidationMode.CREATE)
                .build();

        beanValidationService.validate(context);
        validate(context, currency);
    }

    @Validation
    public void validateUpdate(ValidationContext validationContext, Currency currency) {
        ValidationContext context = validationContext
                .subContext(currency)
                .withMode(ValidationMode.UPDATE)
                .build();

        beanValidationService.validate(context);
        validate(context, currency);
        validateVersion(context, currency, em.find(Currency.class, currency.getId()));
    }
    
    private void validate(ValidationContext context, Currency  currency) {
        if (context.isReachable("currencyCode")) {
            Collection<String> availableFieldValues = CurrencySource.getCurrencyCodes();
            if (availableFieldValues == null || !availableFieldValues.contains(currency.getCurrencyCode())) {
                context
                    .addConstraintViolation("errors.field.invalid")
                    .withPath("currencyCode");
            }
        }
        BigDecimal rate = currency.getRate();
        if (Source.MANUAL.equals(currency.getSource()) && rate == null) {
            context
                    .addConstraintViolation("errors.field.required")
                    .withPath("rate");
        }
        if (context.isReachable("rate")) {
            context
                .validator(FractionDigitsValidator.class)
                .withPath("rate")
                .withFraction(5)
                .validate(rate);
        }

        if (context.props("currencyCode", "source").reachableAndNoViolations()) {
            Currency existing = currency.getId() == null ? null : em.find(Currency.class, currency.getId());
            if (currency.getSource() == Source.FEED && (existing == null || existing.getSource() != Source.FEED)) {
                Map<Currency,BigDecimal> rates;
                try {
                    rates = currencyExchangeService.fetchFromFeed(Collections.singleton(currency));
                } catch (IOException e) {
                    rates = Collections.emptyMap();
                }

                if (rates.isEmpty()) {
                    context.addConstraintViolation("Currency.warning.noCurrencyInFeed")
                            .withPath("source");
                }
            }
        }
    }
    
    private void validateVersion(ValidationContext context, Currency currency, Currency existing) {
        if (context.isReachable("version") && !ObjectUtils.equals(currency.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version")
                .withValue(currency.getVersion())
                .withPath("version");
        }
    }

}
