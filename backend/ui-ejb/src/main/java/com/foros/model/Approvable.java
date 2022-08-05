package com.foros.model;

import com.foros.model.security.User;
import com.foros.model.security.Statusable;

import java.util.Date;

public interface Approvable extends Statusable{
    ApproveStatus getQaStatus();

    User getQaUser();

    Date getQaDate();
}
