package com.foros.session.channel;

import static com.foros.config.ConfigParameters.DISCOVER_CUSTOMIZATION_FILES_CHECK_PERIOD;

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
public class DiscoverCustomizationFileCheckerTimedBean extends AbstractTimedBean {
    private static final String TIMER_NAME = "DiscoverCustomizationChecker";

    @EJB
    private DiscoverCustomizationFileChecker discoverCustomizationFileChecker;

    @EJB
    private ConfigService configService;

    @PostConstruct
    public void init() {
        Config config = configService.detach();
        long interval = config.get(DISCOVER_CUSTOMIZATION_FILES_CHECK_PERIOD);

        startTimer(TIMER_NAME, interval);
    }

    @Override
    protected void proceed(Timer timer) {
        discoverCustomizationFileChecker.proceed();
    }
}
