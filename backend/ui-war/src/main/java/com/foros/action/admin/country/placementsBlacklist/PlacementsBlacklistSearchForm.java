package com.foros.action.admin.country.placementsBlacklist;

import com.foros.action.SearchForm;

public class PlacementsBlacklistSearchForm extends SearchForm {
    private String url;
    private String countryCode;

    public PlacementsBlacklistSearchForm() {
        setPageSize(1000);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return "PlacementsBlacklistSearchForm [url=" + url + ", countryCode=" + countryCode + "]";
    }
}
