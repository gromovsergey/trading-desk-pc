package com.foros.jaxb.adapters;

import com.foros.model.Country;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CountryAdapter extends XmlAdapter<String, Country> {

    @Override
    public String marshal(Country c) throws Exception {
        return c.getCountryCode();
    }

    @Override
    public Country unmarshal(String code) throws Exception {
        return new Country(code);
    }

}
