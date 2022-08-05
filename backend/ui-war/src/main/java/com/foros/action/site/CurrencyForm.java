package com.foros.action.site;

import com.foros.action.IdNameVersionForm;

public class CurrencyForm extends IdNameVersionForm {
    private String[] selectedCountries;
    private String currencyCode;

    public CurrencyForm() {
    }

    public String[] getSelectedCountries() {
        return selectedCountries;
    }

    public void setSelectedCountries(String[] selectedCountries) {
        this.selectedCountries = selectedCountries;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
