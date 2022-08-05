package com.foros.jaxb.adapters;

import com.foros.model.campaign.CcgRate;
import com.foros.util.StringUtil;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CcgRateXmlAdapter extends XmlAdapter<XMLRate, CcgRate> {

    @Override
    public XMLRate marshal(CcgRate rate) throws Exception {
        if (rate == null) {
            return null;
        }

        XMLRate xmlRate = new XMLRate();
        xmlRate.setRateType(rate.getRateType());
        xmlRate.setEffectiveDate(rate.getEffectiveDate());
        BigDecimal value = null;
        switch (rate.getRateType()) {
        case CPA:
            value = rate.getCpa();
            break;
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
    public CcgRate unmarshal(XMLRate xmlRate) throws Exception {
        if (xmlRate.getRateType() == null) {
            throw new LocalizedParseException("errors.required", StringUtil.getLocalizedString("ccg.rate.type"));
        }

        CcgRate rate = new CcgRate();
        rate.setRateType(xmlRate.getRateType());
        rate.setEffectiveDate(xmlRate.getEffectiveDate());
        BigDecimal value = xmlRate.getValue();
        switch (xmlRate.getRateType()) {
        case CPA:
            rate.setCpa(value);
            break;
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
