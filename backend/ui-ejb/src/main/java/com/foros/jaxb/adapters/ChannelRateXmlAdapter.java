package com.foros.jaxb.adapters;

import com.foros.model.channel.ChannelRate;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ChannelRateXmlAdapter extends XmlAdapter<XMLRate, ChannelRate> {

    @Override
    public XMLRate marshal(ChannelRate rate) throws Exception {
        if (rate == null) {
            return null;
        }

        XMLRate xmlRate = new XMLRate();
        xmlRate.setRateType(rate.getRateType());
        xmlRate.setEffectiveDate(rate.getEffectiveDate());
        BigDecimal value = null;
        switch (rate.getRateType()) {        
        case CPC:
            value = rate.getCpc();
            break;
        case CPM:
            value = rate.getCpm();
            break;        
        }
        xmlRate.setValue(value);
        return xmlRate;
    }

    @Override
    public ChannelRate unmarshal(XMLRate xmlRate) throws Exception {
        ChannelRate rate = new ChannelRate();
        rate.setRateType(xmlRate.getRateType());
        rate.setEffectiveDate(xmlRate.getEffectiveDate());
        BigDecimal value = xmlRate.getValue();
        switch (xmlRate.getRateType()) {
        case CPC:
            rate.setCpc(value);
            break;
        case CPM:
            rate.setCpm(value);
            break;        
        }
        return rate;
    }

}
