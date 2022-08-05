package com.foros.session.account.yandex.brand;

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
public class YandexTnsBrandCheckerMonitoringBean extends AbstractMonitoringBean {
    @EJB
    private YandexTnsBrandTimedBean yandexBrandCheckerTimed;

    @PostConstruct
    public void init() {
        startMonitoring(JmxBeanName.YANDEX_TNS_BRAND_CHECKER_NAME.getValue(), yandexBrandCheckerTimed.getInterval());
    }

    @Override
    protected long getLastProcessedTime() {
        return yandexBrandCheckerTimed.getLastProcessedTime();
    }

    @Override
    protected boolean isActive() {
        return yandexBrandCheckerTimed.isActive();
    }
}
