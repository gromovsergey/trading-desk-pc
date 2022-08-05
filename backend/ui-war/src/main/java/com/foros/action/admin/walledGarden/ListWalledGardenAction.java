package com.foros.action.admin.walledGarden;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.admin.WalledGarden;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.util.CountryHelper;
import com.foros.util.StringUtil;

import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

public class ListWalledGardenAction extends BaseActionSupport {
    @EJB
    private WalledGardenService walledGardenService;

    private Map<String, String> countries;
    private List<WalledGarden> entities;

    private String countryCode;

    @ReadOnly
    @Restrict(restriction = "WalledGarden.view")
    public String list() {
        return SUCCESS;
    }

    @ReadOnly
    public String listCurrent() {
        entities = StringUtil.isPropertyEmpty(countryCode) ?
                    walledGardenService.findAllWithDependencies() :
                    walledGardenService.findWithDependancesByCountryCode(countryCode);
        return SUCCESS;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<WalledGarden> getEntities() {
        return entities;
    }

    public Map<String, String> getCountries() {
        if (countries == null) {
            countries = CountryHelper.populateCountries();
        }

        return countries;
    }
}
