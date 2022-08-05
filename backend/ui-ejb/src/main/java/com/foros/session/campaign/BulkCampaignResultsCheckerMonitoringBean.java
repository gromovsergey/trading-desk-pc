package com.foros.session.campaign;

import static com.foros.monitoring.JmxBeanName.BULK_CAMPAIGN_RESULTS_CHECKER_NAME;
import com.foros.monitoring.AbstractMonitoringBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@LocalBean
@Singleton
@Startup
public class BulkCampaignResultsCheckerMonitoringBean extends AbstractMonitoringBean {
    @EJB
    private BulkCampaignResultsCheckerTimedBean bulkCampaignResultsCheckerTimed;

    @PostConstruct
    public void init() {
        startMonitoring(BULK_CAMPAIGN_RESULTS_CHECKER_NAME.getValue(), bulkCampaignResultsCheckerTimed.getInterval());
    }

    @Override
    protected long getLastProcessedTime() {
        return bulkCampaignResultsCheckerTimed.getLastProcessedTime();
    }

    @Override
    protected boolean isActive() {
        return bulkCampaignResultsCheckerTimed.isActive();
    }
}
