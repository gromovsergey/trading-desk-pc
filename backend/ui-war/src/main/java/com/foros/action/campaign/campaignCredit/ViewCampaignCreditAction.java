package com.foros.action.campaign.campaignCredit;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocationTO;
import com.foros.session.campaignCredit.CampaignCreditAllocationService;
import com.foros.session.campaignCredit.CampaignCreditService;
import com.foros.session.campaignCredit.CampaignCreditStatsTO;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;
import java.util.List;

public class ViewCampaignCreditAction extends BaseActionSupport implements RequestContextsAware, ModelDriven<CampaignCredit> {
    @EJB
    private CampaignCreditService campaignCreditService;

    @EJB
    private CampaignCreditAllocationService campaignCreditAllocationService;

    private Long id;

    private CampaignCredit campaignCredit;
    private CampaignCreditStatsTO campaignCreditStats;
    private List<CampaignCreditAllocationTO> allocations;

    @ReadOnly
    public String view() {
        campaignCredit = campaignCreditService.view(id);
        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampaignCreditStatsTO getCampaignCreditStats() {
        if (campaignCreditStats == null) {
            campaignCreditStats = campaignCreditService.getStats(campaignCredit.getId());
        }
        return campaignCreditStats;
    }

    public List<CampaignCreditAllocationTO> getAllocations() {
        if (allocations == null) {
            allocations = campaignCreditAllocationService.findCreditAllocations(campaignCredit.getId());
        }
        return allocations;
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getAdvertiserContext().switchTo(campaignCredit.getAccount().getId());
    }

    @Override
    public CampaignCredit getModel() {
        return campaignCredit;
    }
}
