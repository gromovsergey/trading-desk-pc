package com.foros.action.admin.walledGarden;

import com.foros.action.BaseActionSupport;
import com.foros.model.admin.WalledGarden;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.util.CountryHelper;

import com.opensymphony.xwork2.ModelDriven;
import java.util.Map;
import javax.ejb.EJB;

public abstract class EditSaveWalledGardenActionBase extends BaseActionSupport implements ModelDriven<WalledGarden> {
    @EJB
    protected WalledGardenService walledGardenService;

    protected WalledGarden entity;

    private Map<String, String> countries;

    public WalledGarden getEntity() {
        return getModel();
    }

    public Map<String, String> getCountries() {
        if (countries == null) {
            countries = CountryHelper.populateCountries();
        }

        return countries;
    }

    @Override
    public WalledGarden getModel() {
        return entity;
    }
}
