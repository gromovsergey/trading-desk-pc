package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.model.CurrencyInfo;
import com.foros.util.CurrencyHelper;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class CurrencyByCodeXmlAction extends AbstractXmlAction<CurrencyInfo> {

    private String currencyCode;

    @RequiredStringValidator(key = "errors.required", message = "Currency.currencyCode")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public CurrencyInfo generateModel() throws ProcessException {
        String currency = CurrencyHelper.resolveCurrencyName(currencyCode);
        String symbol = CurrencyHelper.getCurrencySymbol(currencyCode);
        int fractionDigits = CurrencyHelper.getCurrencyFractionDigits(currencyCode);
        return new CurrencyInfo(currency, symbol, fractionDigits);
    }

}