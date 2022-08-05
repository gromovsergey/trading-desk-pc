package com.foros.session.site;

import com.foros.model.Status;
import com.foros.model.site.WDTag;
import com.foros.session.DisplayStatusEntityTO;

public class WDTagTO extends DisplayStatusEntityTO {
    private Long width;
    private Long height;

    public WDTagTO(Long id, String name, char status, Long width, Long height) {
        super(id, name, status, WDTag.getDisplayStatus(Status.valueOf(status)));
        this.width = width;
        this.height = height;
    }

    public Long getWidth() {
        return width;
    }

    public Long getHeight() {
        return height;
    }
}
