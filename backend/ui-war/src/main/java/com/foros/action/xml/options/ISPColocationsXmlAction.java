package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.ISPColocationConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.ISPColocationTO;

import java.util.Collection;
import javax.ejb.EJB;

public class ISPColocationsXmlAction extends AbstractOptionsAction<ISPColocationTO> {
    @EJB
    private CampaignCreativeGroupService ccgService;

    private String countryCode;
    private boolean testAccount;
    private String name;

    public ISPColocationsXmlAction() {
        super(new ISPColocationConverter(), new OptionStatusFilter(false));
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isTestAccount() {
        return testAccount;
    }

    public void setTestAccount(boolean testAccount) {
        this.testAccount = testAccount;
    }

    public CampaignCreativeGroupService getCcgService() {
        return ccgService;
    }

    public void setCcgService(CampaignCreativeGroupService ccgService) {
        this.ccgService = ccgService;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Collection<ISPColocationTO> getOptions() throws ProcessException {
        return ccgService.findColocationsByName(name, countryCode, testAccount, AUTOCOMPLETE_SIZE);
    }
}