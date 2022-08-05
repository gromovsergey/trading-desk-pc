package com.foros.session;

import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class QAEntityTO extends DisplayStatusEntityTO {
    private ApproveStatus qaStatus;

    public QAEntityTO() {
        super();
    }

    public QAEntityTO(Long id, String name, char status, char qaStatus, DisplayStatus displayStatusId) {
        super(id, name, status, displayStatusId);
        this.qaStatus = ApproveStatus.valueOf(qaStatus);
    }

    public ApproveStatus getQaStatus() {
        return qaStatus;
    }
}
