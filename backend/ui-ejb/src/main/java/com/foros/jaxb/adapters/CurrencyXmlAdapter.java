package com.foros.jaxb.adapters;

import com.foros.model.currency.Currency;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CurrencyXmlAdapter extends XmlAdapter<CurrencyInfo, Currency> {

    @Override
    public Currency unmarshal(CurrencyInfo currencyInfo) throws Exception {
        return null;
    }

    @Override
    public CurrencyInfo marshal(Currency currency) throws Exception {
        return new CurrencyInfo(currency);
    }
}
