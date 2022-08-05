package com.foros.session;

import com.foros.model.DisplayStatus;
import com.foros.model.Status;

public class TreeFilterElementTO extends EntityTO {

    private DisplayStatus displayStatus;
    private boolean hasChildren;

    public TreeFilterElementTO(Long id, String name, Status status, DisplayStatus displayStatus, boolean hasChildren) {
        super(id, name, status);
        this.displayStatus = displayStatus;
        this.hasChildren = hasChildren;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }
}
