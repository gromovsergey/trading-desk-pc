package com.foros.action.xml.options;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.campaignCredit.CampaignCreditService;

import javax.ejb.EJB;
import java.util.Collection;

public class CampaignsForCreditAllocationXmlAction extends AbstractOptionsAction<EntityTO> {
    @EJB
    private CampaignCreditService campaignCreditService;

    private Long advertiserId;

    public CampaignsForCreditAllocationXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(false));
    }

    @RequiredStringValidator(key = "errors.required", message = "value.advertiserId")
    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        return campaignCreditService.getCampaignsForCreditAllocation(advertiserId);
    }
}
