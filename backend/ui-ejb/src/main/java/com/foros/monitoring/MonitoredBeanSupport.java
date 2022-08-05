package com.foros.monitoring;

public interface MonitoredBeanSupport {

    boolean isActive();

    long getLastCompletionTime();

    long getAliveCheckInterval();

}
