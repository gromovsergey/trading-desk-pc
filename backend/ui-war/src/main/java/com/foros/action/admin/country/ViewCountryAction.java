package com.foros.action.admin.country;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.StringUtil;

public class ViewCountryAction extends CountryActionSupport implements BreadcrumbsSupport {

    private String id;

    @ReadOnly
    @Restrict(restriction = "Country.view")
    public String view() {
        if (StringUtil.isNumber(id)) {
            country = countryService.findByCountryId(StringUtil.convertToLong(id));
        } else {
            country = countryService.find(id);
        }

        prepareConfigValues();
        prepareAddressAndFile();

        return SUCCESS;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CountriesBreadcrumbsElement()).add(new CountryBreadcrumbsElement(country));
    }
}
