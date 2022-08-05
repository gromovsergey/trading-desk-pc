package com.foros.session.textad;

import com.foros.config.Config;
import com.foros.config.ConfigService;
import com.foros.timer.AbstractTimedBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;

import static com.foros.config.ConfigParameters.TEXT_AD_IMAGE_FILES_CHECK_PERIOD;

@LocalBean
@Singleton
@Startup
public class TextAdImageFileCheckerTimedBean extends AbstractTimedBean {
    private static final String TIMER_NAME = "TextAdImageChecker";

    @EJB
    private ConfigService configService;

    @EJB
    private TextAdImageFileChecker textAdImageFileChecker;

    @PostConstruct
    public void init() {
        Config config = configService.detach();
        long interval = config.get(TEXT_AD_IMAGE_FILES_CHECK_PERIOD);

        startTimer(TIMER_NAME, interval);
    }

    @Override
    protected void proceed(Timer timer) {
        textAdImageFileChecker.proceed();
    }
}
