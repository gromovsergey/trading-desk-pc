package com.foros.action.xml.options;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.session.admin.country.ctra.CTRAlgorithmService;

import javax.ejb.EJB;

public class AdvertiserIdByNameXMLAction extends AbstractXmlAction<Long> {

    private String name;

    private String countryCode;

    @EJB
    private CTRAlgorithmService ctrAlgorithmService;

    @Override
    protected Long generateModel() throws ProcessException {
        return ctrAlgorithmService.findAdvertiserId(name, countryCode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
