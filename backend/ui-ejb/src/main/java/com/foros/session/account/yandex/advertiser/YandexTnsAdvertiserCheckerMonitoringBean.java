package com.foros.session.account.yandex.advertiser;

import com.foros.monitoring.AbstractMonitoringBean;
import com.foros.monitoring.JmxBeanName;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@LocalBean
@Singleton
@Startup
public class YandexTnsAdvertiserCheckerMonitoringBean extends AbstractMonitoringBean {
    @EJB
    private YandexTnsAdvertiserTimedBean yandexAdvertiserCheckerTimed;

    @PostConstruct
    public void init() {
        startMonitoring(JmxBeanName.YANDEX_TNS_ADVERTISER_CHECKER_NAME.getValue(), yandexAdvertiserCheckerTimed.getInterval());
    }

    @Override
    protected long getLastProcessedTime() {
        return yandexAdvertiserCheckerTimed.getLastProcessedTime();
    }

    @Override
    protected boolean isActive() {
        return yandexAdvertiserCheckerTimed.isActive();
    }
}
