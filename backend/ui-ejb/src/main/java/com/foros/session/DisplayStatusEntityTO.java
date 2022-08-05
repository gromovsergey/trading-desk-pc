package com.foros.session;

import com.foros.model.DisplayStatus;

public class DisplayStatusEntityTO extends EntityTO {
    private DisplayStatus displayStatus;

    public DisplayStatusEntityTO() {
        super();
    }

    public DisplayStatusEntityTO(Long id, String name, char status, DisplayStatus displayStatus) {
        super(id, name, status);
        
        this.displayStatus = displayStatus;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

}
