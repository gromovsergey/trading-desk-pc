package com.foros.action.admin.country.placementsBlacklist;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.Country;
import com.foros.util.CountryHelper;

public class EntityPlacementsBlacklistBreadcrumbsElement extends EntityBreadcrumbsElement {
    public EntityPlacementsBlacklistBreadcrumbsElement(Country country) {
        super("admin.placementsBlacklist", country.getCountryCode(), CountryHelper.resolveCountryName(country.getCountryCode()), "Country/PlacementsBlacklist/view");
    }
}
