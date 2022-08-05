package com.foros.session.status;

import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.session.DisplayStatusEntityTO;

public class ApprovableEntityTO extends DisplayStatusEntityTO {
    private ApproveStatus qaStatus;

    public ApprovableEntityTO(Long id, String name, char status, char qaStatus, DisplayStatus displayStatus) {
        super(id, name, status, displayStatus);
        this.qaStatus = ApproveStatus.valueOf(qaStatus);
    }

    public ApproveStatus getQaStatus() {
        return qaStatus;
    }

    public void setQaStatus(ApproveStatus qaStatus) {
        this.qaStatus = qaStatus;
    }
}
