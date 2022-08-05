package com.foros.service.timed;

import javax.ejb.Local;

@Local
public interface TimedManagerService {

    public <T> boolean canProcess(Class<T> service, Long period);
}
