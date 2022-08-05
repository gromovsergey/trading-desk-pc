package com.foros.session.campaign;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.EJB;

@Singleton(name = "BulkCampaignResultsChecker")
@Startup
public class BulkCampaignResultsCheckerBean implements BulkCampaignResultsChecker {
    private static final Logger logger = Logger.getLogger(BulkCampaignResultsCheckerBean.class.getName());

    @EJB
    private BulkCampaignToolsService bulkCampaignToolsService;

    @Override
    public void proceed() {
        logger.log(Level.INFO, "Bulk campaign results checker running.");
        bulkCampaignToolsService.deleteObsoleteValidatedResults();
    }
}
