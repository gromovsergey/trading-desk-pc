package com.foros.session.admin;

import com.foros.model.currency.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class CurrencyConverter {

    private Currency target;
    private Map<Long, BigDecimal> currencyRates;
    private final BigDecimal fromBase;

    public CurrencyConverter(Currency target, Map<Long, BigDecimal> currencyRates) {
         this.target = target;
         this.currencyRates = currencyRates;
         this.fromBase = currencyRates.get(target.getId());
    }

    public Currency getTarget() {
        return target;
    }

    public BigDecimal convert(Long currencyId, BigDecimal value) {
        if (currencyId.equals(target.getId())) {
            return value;
        }

        BigDecimal toBase = currencyRates.get(currencyId);
        return value.multiply(fromBase).divide(toBase, target.getFractionDigits(), RoundingMode.HALF_UP);
    }

    public BigDecimal convertFromBase(BigDecimal value) {
        return value.multiply(fromBase).setScale(target.getFractionDigits(), RoundingMode.HALF_UP);
    }

}
