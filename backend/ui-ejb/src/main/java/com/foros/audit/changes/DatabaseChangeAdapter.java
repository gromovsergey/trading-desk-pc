package com.foros.audit.changes;

import javax.ejb.Local;

@Local
public interface DatabaseChangeAdapter {
    public void proceed();
}
