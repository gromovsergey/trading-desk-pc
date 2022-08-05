package com.foros.action.admin.country;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.Country;
import com.foros.util.CountryHelper;

public class CountryBreadcrumbsElement extends EntityBreadcrumbsElement {
    public CountryBreadcrumbsElement(Country country) {
        super("Country.entityName", country.getCountryId(), CountryHelper.resolveCountryName(country.getCountryCode()), "Country/view");
    }
}
