package com.foros.session.creative;

import static com.foros.monitoring.JmxBeanName.TEMPLATE_FILE_CHECKER_NAME;
import com.foros.monitoring.AbstractMonitoringBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@LocalBean
@Singleton
@Startup
public class TemplateFileCheckerMonitoringBean extends AbstractMonitoringBean {
    @EJB
    private TemplateFileCheckerTimedBean templateFileCheckerTimed;

    @PostConstruct
    public void init() {
        startMonitoring(TEMPLATE_FILE_CHECKER_NAME.getValue(), templateFileCheckerTimed.getInterval());
    }

    @Override
    protected long getLastProcessedTime() {
        return templateFileCheckerTimed.getLastProcessedTime();
    }

    @Override
    protected boolean isActive() {
        return templateFileCheckerTimed.isActive();
    }
}
