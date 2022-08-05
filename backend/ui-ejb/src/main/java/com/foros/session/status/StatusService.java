package com.foros.session.status;

import com.foros.model.ApprovableEntity;
import com.foros.model.StatusEntityBase;
import com.foros.model.security.Statusable;
import com.foros.session.StatusAction;

import javax.ejb.Local;

@Local
public interface StatusService {
    <T extends StatusEntityBase> void delete(T entity);

    <T extends StatusEntityBase> void undelete(T entity);

    <T extends StatusEntityBase> void activate(T entity);

    <T extends StatusEntityBase> void inactivate(T entity);
    
    boolean isActionAvailable(Statusable entity, StatusAction action);

    void makePendingOnChange(ApprovableEntity entity, boolean isStatusChanged);    
}
