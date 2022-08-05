package com.foros.service.timed;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

public abstract class AbstractScheduledTimedBean {
    private static final Logger logger = Logger.getLogger(AbstractScheduledTimedBean.class.getName());

    @Resource
    private TimerService timerService;

    @EJB
    private TimedManagerService timedManagerService;

    private Long checkPeriod;

    private String timerName;

    protected void init(String schedule, Long checkPeriod, String timerName) {
        this.checkPeriod = checkPeriod;
        this.timerName = timerName;
        try {
            ScheduleExpression scheduleExpression = CronStyleScheduleParseUtil.parse(schedule);
            TimerConfig timerConfig = new TimerConfig(timerName, false);
            Timer timer = timerService.createCalendarTimer(scheduleExpression, timerConfig);
            onScheduleInitialized(timer);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not start {0}. Possibly wrong value for schedule provided", this.timerName);
        }
    }

    protected void onTimeout(Timer timer) {
        if (timedManagerService.canProcess(getClass(), checkPeriod)) {
            onStartProceeding(timer);
            proceed(timer);
            onCompletion(timer);
        } else {
            onProceedCancelled();
        }
    }

    protected void onScheduleInitialized(Timer timer) {
        logger.log(Level.INFO, "{0} schedule initialized. Next run is scheduled at {1}", new Object[]{timerName, timer.getNextTimeout()});
    }

    protected abstract void proceed(Timer timer);

    protected void onProceedCancelled() {
        logger.log(Level.INFO, "{0} run is cancelled. Possibly other instance is captured it already", timerName);
    }

    protected void onStartProceeding(Timer timer) {
        logger.log(Level.INFO, "{0} work started", timerName);
    }

    protected void onCompletion(Timer timer) {
        logger.log(Level.INFO, "{0} work complete. Next run is scheduled at {1}", new Object[]{timerName, timer.getNextTimeout()});
    }
}
