package com.foros.model;

import com.foros.annotations.Audit;
import com.foros.audit.serialize.serializer.primitive.StatusAuditSerializer;
import com.foros.model.security.Statusable;
import com.foros.util.EntityUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "status" })
@MappedSuperclass
public abstract class StatusEntityBase extends VersionEntityBase implements Statusable {
    @Audit(serializer = StatusAuditSerializer.class)
    @Column(name = "STATUS", nullable = false)
    protected char status;

    @Override
    public abstract Status getParentStatus();

    @Override
    public Status[] getAllowedStatuses() {
        return EntityUtils.getAllowedStatuses(this.getClass());
    }

    @Override
    public boolean isStatusAllowed(Status status) {
        boolean allowed = false;
        Status[] allowedStatuses = EntityUtils.getAllowedStatuses(this.getClass());

        for (Status s : allowedStatuses) {
            if (s == status) {
                allowed = true;
                break;
            }
        }
        return allowed;
    }

    @Override
    public Status getStatus() {
        return Status.valueOf(status);
    }

    @Override
    public void setStatus(Status status) {
        if (isStatusAllowed(status)) {
            this.status = status.getLetter();
        } else {
            throw new IllegalArgumentException("Status is not allowed");
        }

        this.registerChange("status");
    }

    @Override
    public Status getInheritedStatus() {
        return getStatus().combine(getParentStatus());
    }
}
