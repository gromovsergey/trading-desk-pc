package com.foros.monitoring;

import javax.ejb.Local;

@Local
public interface TimedManagerServiceM {
    public String getStatus();
}
