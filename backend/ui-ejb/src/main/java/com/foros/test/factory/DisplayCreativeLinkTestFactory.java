package com.foros.test.factory;

import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class DisplayCreativeLinkTestFactory extends CampaignCreativeTestFactory {
    @EJB
    private DisplayCCGTestFactory displayCCGTF;

    @EJB
    private DisplayCreativeTestFactory displayCreativeTF;

    @Override
    protected void prepare(CampaignCreative campaignCreative) {
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent();
        campaignCreative.setCreativeGroup(ccg);
        Creative creative = displayCreativeTF.createPersistent(ccg.getAccount());
        campaignCreative.setCreative(creative);
    }


    public CampaignCreative createPersistent(CampaignCreativeGroup ccg) {
        CampaignCreative campaignCreative = create();
        campaignCreative.setCreativeGroup(ccg);
        Creative creative = displayCreativeTF.createPersistent(ccg.getAccount());
        campaignCreative.setCreative(creative);

        persist(campaignCreative);
        return campaignCreative;
    }
}
