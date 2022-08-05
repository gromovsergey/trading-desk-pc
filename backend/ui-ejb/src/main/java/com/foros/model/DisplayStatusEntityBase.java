package com.foros.model;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;

import javax.persistence.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class DisplayStatusEntityBase extends StatusEntityBase implements DisplayStatusEntity {
    @Column(name = "DISPLAY_STATUS_ID", nullable = false, updatable = false)
    @ChangesInspection(type = InspectionType.NONE)
    protected Long displayStatusId;

    @Override
    @XmlTransient
    abstract public DisplayStatus getDisplayStatus();

    public void setDisplayStatusId(Long displayStatusId) {
        this.displayStatusId = displayStatusId;
        this.registerChange("displayStatusId");
    }

    @XmlTransient
    public Long getDisplayStatusId() {
        return displayStatusId;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        if (displayStatus != null) {
            setDisplayStatusId(displayStatus.getId());
        }
    }

    //TODO must be protected
    public static Map<Long, DisplayStatus> getDisplayStatusMap(DisplayStatus ... displayStatuses) {
        HashMap<Long, DisplayStatus> result = new HashMap<Long, DisplayStatus>();

        for (DisplayStatus displayStatus : displayStatuses) {
            result.put(displayStatus.getId(), displayStatus);
        }

        return Collections.unmodifiableMap(result);
    }
}
