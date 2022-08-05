package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class CountryInfoXmlAction extends AbstractXmlAction<String> {

    private String countryCode;

    @RequiredStringValidator(key = "errors.required", message = "countryCode")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String generateModel() throws ProcessException {
        return getCountryCode();
    }

}