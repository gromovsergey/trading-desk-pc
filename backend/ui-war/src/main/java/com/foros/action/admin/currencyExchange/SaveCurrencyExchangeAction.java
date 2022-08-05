package com.foros.action.admin.currencyExchange;

import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.List;

public class SaveCurrencyExchangeAction extends EditCurrencyExchangeActionSupport {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
        .add("currencyExchangeRates[(#index)](#path)", "'manualRates[' + groups[0] + ']' + groups[1]", "violation.message")
        .rules();

    public String save() throws Exception {
        service.update(manualRates, previousEffectiveDate);
        return SUCCESS;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }
}
