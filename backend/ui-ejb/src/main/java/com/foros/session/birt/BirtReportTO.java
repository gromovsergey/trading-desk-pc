package com.foros.session.birt;

import com.foros.session.bulk.IdNameTO;

public class BirtReportTO extends IdNameTO {

    public BirtReportTO(Long id, String name) {
        super(id, name);
    }

    private boolean updatable = false;

    private boolean viewable = false;

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public boolean isViewable() {
        return viewable;
    }

    public void setViewable(boolean viewable) {
        this.viewable = viewable;
    }
}
