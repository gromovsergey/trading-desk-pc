package com.foros.session.campaign;

import com.foros.model.Status;
import com.foros.session.EntityTO;

public class CampaignScheduleTO extends EntityTO {
    private String name;
    private Long timeFrom;
    private Long timeTo;

    public CampaignScheduleTO(String name, Long timeFrom, Long timeTo, char status) {
        this.name = name;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        super.setStatus(Status.valueOf(status));
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Long timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Long timeTo) {
        this.timeTo = timeTo;
    }
}
