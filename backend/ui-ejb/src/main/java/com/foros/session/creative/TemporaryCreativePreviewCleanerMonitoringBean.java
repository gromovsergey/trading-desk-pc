package com.foros.session.creative;

import com.foros.monitoring.AbstractMonitoringBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import static com.foros.monitoring.JmxBeanName.TMP_CREATIVE_PREVIEW_CLEANER_NAME;

@LocalBean
@Singleton
@Startup
public class TemporaryCreativePreviewCleanerMonitoringBean extends AbstractMonitoringBean {
    @EJB
    private TemporaryCreativePreviewCleanerTimedBean temporaryCreativePreviewCleanerTimed;

    @PostConstruct
    public void init() {
        startMonitoring(TMP_CREATIVE_PREVIEW_CLEANER_NAME.getValue(), temporaryCreativePreviewCleanerTimed.getInterval());
    }

    @Override
    protected long getLastProcessedTime() {
        return temporaryCreativePreviewCleanerTimed.getLastProcessedTime();
    }

    @Override
    protected boolean isActive() {
        return temporaryCreativePreviewCleanerTimed.isActive();
    }
}
