package com.foros.model.security;

import com.foros.model.Status;

public interface Statusable {

    Status[] getAllowedStatuses();

    boolean isStatusAllowed(Status status);

    Status getStatus();

    void setStatus(Status status);

    Status getParentStatus();

    Status getInheritedStatus();

}
