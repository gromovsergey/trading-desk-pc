package com.foros.reporting.serializer.formatter;

import com.foros.session.ServiceLocator;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;

import java.math.BigDecimal;
import java.util.Date;

public class CurrencyConverterFormatter extends CurrencyValueFormatterSupport {

    private CurrencyConverter crossRate;

    public CurrencyConverterFormatter(String currencyCode) {
        CurrencyExchangeService exchangeService = ServiceLocator.getInstance().lookup(CurrencyExchangeService.class);
        this.crossRate = exchangeService.getCrossRate(currencyCode, new Date());
    }

    @Override
    protected String getCurrencyCode(FormatterContext context) {
        return crossRate.getTarget().getCurrencyCode();
    }


    @Override
    protected BigDecimal convertValue(BigDecimal value) {
        return crossRate.convertFromBase(value);
    }
}
