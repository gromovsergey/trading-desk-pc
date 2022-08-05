package com.foros.session.status;

import com.foros.model.Approvable;
import com.foros.model.ApprovableEntity;

import javax.ejb.Local;

@Local
public interface ApprovalService {
    <T extends ApprovableEntity> T approve(T entity);

    <T extends ApprovableEntity> T decline(T entity, String reason);

    boolean isActionAvailable(Approvable entity, ApprovalAction action);

    void makePendingOnChange(ApprovableEntity entity);
}
