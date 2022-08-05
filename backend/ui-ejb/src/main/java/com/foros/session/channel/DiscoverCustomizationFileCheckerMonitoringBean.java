package com.foros.session.channel;

import static com.foros.monitoring.JmxBeanName.DISCOVER_CUSTOMIZATION_FILE_CHECKER_NAME;
import com.foros.monitoring.AbstractMonitoringBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@LocalBean
@Singleton
@Startup
public class DiscoverCustomizationFileCheckerMonitoringBean extends AbstractMonitoringBean {
    @EJB
    private DiscoverCustomizationFileCheckerTimedBean discoverCustomizationFileCheckerTimed;

    @PostConstruct
    public void init() {
        startMonitoring(DISCOVER_CUSTOMIZATION_FILE_CHECKER_NAME.getValue(), discoverCustomizationFileCheckerTimed.getInterval());
    }

    @Override
    protected long getLastProcessedTime() {
        return discoverCustomizationFileCheckerTimed.getLastProcessedTime();
    }

    @Override
    protected boolean isActive() {
        return discoverCustomizationFileCheckerTimed.isActive();
    }
}

