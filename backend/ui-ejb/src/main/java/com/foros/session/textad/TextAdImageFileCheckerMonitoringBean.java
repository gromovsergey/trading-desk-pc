package com.foros.session.textad;

import com.foros.monitoring.AbstractMonitoringBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import static com.foros.monitoring.JmxBeanName.TEXT_AD_IMAGE_FILE_CHECKER_NAME;

@LocalBean
@Singleton
@Startup
public class TextAdImageFileCheckerMonitoringBean extends AbstractMonitoringBean {
    @EJB
    private TextAdImageFileCheckerTimedBean textAdImageCheckerTimed;

    @PostConstruct
    public void init() {
        startMonitoring(TEXT_AD_IMAGE_FILE_CHECKER_NAME.getValue(), textAdImageCheckerTimed.getInterval());
    }

    @Override
    protected long getLastProcessedTime() {
        return textAdImageCheckerTimed.getLastProcessedTime();
    }

    @Override
    protected boolean isActive() {
        return textAdImageCheckerTimed.isActive();
    }
}
