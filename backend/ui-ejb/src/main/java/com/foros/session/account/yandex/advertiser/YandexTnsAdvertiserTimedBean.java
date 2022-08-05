package com.foros.session.account.yandex.advertiser;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.timer.AbstractTimedBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;

@LocalBean
@Startup
@Singleton
public class YandexTnsAdvertiserTimedBean extends AbstractTimedBean {
    private static final String TIMER_NAME = "YandexTnsAdvertiserChecker";

    @EJB
    private ConfigService configService;

    @EJB
    private YandexTnsAdvertiserService yandexChecker;

    @PostConstruct
    public void init() {
        Config config = configService.detach();
        long interval = config.get(ConfigParameters.YANDEX_TNS_ADVERTISER_CHECK_PERIOD);

        if (interval == 0L) {
            return;
        }
        startTimer(TIMER_NAME, interval);
    }

    @Override
    protected void proceed(Timer timer) {
        yandexChecker.synchronize();
    }
}
