package com.foros.jaxb.adapters;

import com.foros.model.account.MarketplaceType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MarketpalceXmlAdapter extends XmlAdapter<String, MarketplaceType> {

    private static final String FOROS = "EX_WG";

    @Override
    public MarketplaceType unmarshal(String v) throws Exception {
        if (v != null) {
            if (FOROS.equals(v)) {
                return MarketplaceType.FOROS;
            } else {
                return MarketplaceType.valueOf(v);
            }
        }
        return null;
    }

    @Override
    public String marshal(MarketplaceType v) throws Exception {
        return MarketplaceType.FOROS == v ? FOROS : v.toString();
    }

}
