package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.session.NamedTO;
import com.foros.session.admin.country.ctra.CTRAlgorithmService;

import java.util.Collection;
import javax.ejb.EJB;

public class AdvertisersByNameXMLAction extends AbstractOptionsAction<NamedTO> {
    @EJB
    private CTRAlgorithmService ctrAlgorithmService;

    private String countryCode;
    private String name;

    public AdvertisersByNameXMLAction() {
        super(new NamedTOConverter(false));
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Collection<NamedTO> getOptions() throws ProcessException {
        return ctrAlgorithmService.findAdvertisers(name, countryCode, AUTOCOMPLETE_SIZE);
    }
}
