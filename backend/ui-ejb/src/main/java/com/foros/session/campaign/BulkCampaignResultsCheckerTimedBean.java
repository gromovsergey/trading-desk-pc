package com.foros.session.campaign;

import static com.foros.config.ConfigParameters.BULK_CAMPAIGN_RESULTS_CHECK_PERIOD;
import com.foros.config.Config;
import com.foros.config.ConfigService;
import com.foros.timer.AbstractTimedBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;

@LocalBean
@Singleton
@Startup
public class BulkCampaignResultsCheckerTimedBean extends AbstractTimedBean {
    private static final String TIMER_NAME = "BulkCampaignResultsChecker";

    @EJB
    private BulkCampaignResultsChecker bulkCampaignResultsChecker;

    @EJB
    private ConfigService configService;

    @PostConstruct
    public void init() {
        Config config = configService.detach();
        long interval = config.get(BULK_CAMPAIGN_RESULTS_CHECK_PERIOD);

        startTimer(TIMER_NAME, interval);
    }

    @Override
    protected void proceed(Timer timer) {
        bulkCampaignResultsChecker.proceed();
    }
}
