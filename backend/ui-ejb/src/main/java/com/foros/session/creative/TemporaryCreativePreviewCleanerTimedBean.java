package com.foros.session.creative;

import com.foros.config.Config;
import com.foros.config.ConfigService;
import com.foros.timer.AbstractTimedBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;

import static com.foros.config.ConfigParameters.TMP_CREATIVE_PREVIEW_CLEANER_CHECK_PERIOD;

@LocalBean
@Singleton
@Startup
public class TemporaryCreativePreviewCleanerTimedBean extends AbstractTimedBean {
    private static final String TIMER_NAME = "TextAdImageChecker";

    @EJB
    private ConfigService configService;

    @EJB
    private TemporaryCreativePreviewCleaner temporaryCreativePreviewCleaner;

    @PostConstruct
    public void init() {
        Config config = configService.detach();
        long interval = config.get(TMP_CREATIVE_PREVIEW_CLEANER_CHECK_PERIOD);

        startTimer(TIMER_NAME, interval);
    }

    @Override
    protected void proceed(Timer timer) {
        temporaryCreativePreviewCleaner.proceed();
    }
}

