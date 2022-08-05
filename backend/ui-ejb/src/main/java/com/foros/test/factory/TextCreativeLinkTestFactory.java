package com.foros.test.factory;

import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class TextCreativeLinkTestFactory extends CampaignCreativeTestFactory {
    @EJB
    private TextCCGTestFactory textCCGTF;

    @EJB
    private TextCreativeTestFactory textCreativeTF;

    @Override
    protected void prepare(CampaignCreative campaignCreative) {
        CampaignCreativeGroup ccg = textCCGTF.createPersistent();
        campaignCreative.setCreativeGroup(ccg);
        Creative creative = textCreativeTF.createPersistent(ccg.getAccount());
        campaignCreative.setCreative(creative);
    }
}
