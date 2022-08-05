package com.foros.audit.changes;

import static com.foros.config.ConfigParameters.AUDIT_CHECKING_INTERVAL;
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
public class DatabaseChangeAdapterTimedBean extends AbstractTimedBean {
    private static final String TIMER_NAME = "DatabaseChange";

    @EJB
    private DatabaseChangeAdapter databaseChangeAdapter;

    @EJB
    private ConfigService configService;

    @PostConstruct
    public void init() {
        Config config = configService.detach();
        long interval = config.get(AUDIT_CHECKING_INTERVAL);

        startTimer(TIMER_NAME, interval);
    }

    @Override
    protected void proceed(Timer timer) {
        databaseChangeAdapter.proceed();
    }
}
