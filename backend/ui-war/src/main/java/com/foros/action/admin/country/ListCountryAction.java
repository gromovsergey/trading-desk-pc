package com.foros.action.admin.country;

import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.util.CountryHelper;

import java.util.Collection;

public class ListCountryAction extends CountryActionSupport {

    private Collection<CountryCO> entities;

    @ReadOnly
    public String list() {
        this.entities = CountryHelper.sort(countryService.search());
        return SUCCESS;
    }

    public Collection<CountryCO> getEntities() {
        return entities;
    }
}
