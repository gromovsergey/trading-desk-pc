package com.foros.action.campaign.campaignCredit;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.campaign.CampaignCredit;

public class CampaignCreditBreadcrumbsElement  extends EntityBreadcrumbsElement {
    public CampaignCreditBreadcrumbsElement(CampaignCredit credit) {
        super("CampaignCredit.entityName", credit.getId(), String.valueOf(credit.getId()), "campaignCredit/view");
    }
}
