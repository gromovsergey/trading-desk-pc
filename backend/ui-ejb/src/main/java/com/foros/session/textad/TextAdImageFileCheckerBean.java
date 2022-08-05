package com.foros.session.textad;

import com.foros.session.campaign.CampaignCreativeService;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "TextAdImageFileChecker")
@Startup
public class TextAdImageFileCheckerBean implements TextAdImageFileChecker {
    private static final Logger logger = Logger.getLogger(TextAdImageFileCheckerBean.class.getName());

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @Override
    public void proceed() {
        logger.log(Level.INFO, "Text Ad image files checker running.");
        campaignCreativeService.updateAllImagePreviews();
    }
}