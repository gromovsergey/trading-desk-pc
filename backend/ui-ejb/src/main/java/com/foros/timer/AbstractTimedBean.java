package com.foros.timer;

import com.foros.service.timed.TimedManagerService;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

public abstract class AbstractTimedBean implements TimedObject {
    private static final Logger logger = Logger.getLogger(AbstractTimedBean.class.getName());

    private static final long MIN_INTERVAL_DURATION = 1000;
    private static final long SLEEP_INTERVAL_DURATION = 1000;

    private long interval;

    private TimerConfig timerConfig;

    private boolean isActive = false;

    private Timer timer;

    private long lastProcessedTime;

    @Resource
    private TimerService timerService;

    @EJB
    private TimedManagerService timedManagerService;

    protected abstract void proceed(Timer timer);

    public void startTimer(String name, long interval) {
        this.interval = interval;

        try {
            isActive = canProcess();
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Failed to retrieve status. Timer for " + getClass().getName() + "' was set to 'Not Active')", e);
        }

        timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerConfig.setInfo(name);

        lastProcessedTime = new Date().getTime();
        setTimer(interval);
        logger.log(Level.INFO, "Start {0} timer (period = {1} sec).", new Object[]{getClass().getName(), interval / 1000});
    }

    @PreDestroy
    public void stopTimer() {
        timer.cancel();
        logger.log(Level.INFO, "Timer for {0} is stopped", getClass().getName());
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public void ejbTimeout(Timer timer) {
        long startTime = System.currentTimeMillis();

        doProceed(timer);

        lastProcessedTime = System.currentTimeMillis();
        long processedTime = lastProcessedTime - startTime;

        long newInterval = interval - processedTime;
        if (newInterval < 0) {
            logger.log(Level.WARNING, "{0}.proceed() method had taken {1} sec, when a timeout is {2} sec",
                    new Object[]{getClass().getName(), processedTime / 1000, interval / 1000});
        }

        if (newInterval < MIN_INTERVAL_DURATION) {
            newInterval = MIN_INTERVAL_DURATION;
        }

        setTimer(newInterval);
    }

    public long getInterval() {
        return interval;
    }

    public long getLastProcessedTime() {
        return lastProcessedTime;
    }

    public boolean isActive() {
        return isActive;
    }

    private void setTimer(long newIntervalDuration) { 
        timer = timerService.createSingleActionTimer(newIntervalDuration, timerConfig);
    }

    private class ServiceRunner implements Runnable {
        Timer currTimer;

        public ServiceRunner(Timer timer) {
            currTimer = timer;
        }

        @Override
        public void run() {
            try {
                proceed(currTimer);
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Method 'void proceed(Timer timer)' throws an exception. Ignored. Details: ", t);
            }
        }
    }

    private void doProceed(Timer timer) {
        Thread serviceRunner = null;
        try {
            if (!canProcess()) {
                isActive = false;
                return;
            }

            isActive = true;
            serviceRunner = new Thread(new ServiceRunner(timer));
            serviceRunner.start();

            long counter = 0;
            while (serviceRunner.isAlive()) {
                if (counter >= interval) {
                    counter -= interval;
                    lastProcessedTime = new Date().getTime();
                    if (!canProcess()) {
                        serviceRunner.interrupt();
                    }
                }

                Thread.sleep(SLEEP_INTERVAL_DURATION);
                counter += SLEEP_INTERVAL_DURATION;
            }

            serviceRunner.join();
        } catch (Throwable t) {
            if (serviceRunner != null) {
                serviceRunner.interrupt();
            }
            logger.log(Level.SEVERE, "Method 'void proceed(Timer timer)' throws an exception. Ignored. Details: ", t);
        }
    }

    private boolean canProcess() {
        return timedManagerService.canProcess(getClass(), interval);
    }
}
