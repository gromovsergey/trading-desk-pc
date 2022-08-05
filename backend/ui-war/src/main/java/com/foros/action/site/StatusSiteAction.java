package com.foros.action.site;

import com.foros.action.BaseActionSupport;
import com.foros.session.site.SiteService;

import javax.ejb.EJB;

public class StatusSiteAction extends BaseActionSupport {
    @EJB
    private SiteService service;

    private Long id;
    private String declinationReason;

    public String approve() {
        service.approve(id);

        return SUCCESS;
    }

    public String decline() {
        service.decline(id, declinationReason);

        return SUCCESS;
    }

    public String delete() {
        service.delete(id);

        return SUCCESS;
    }

    public String undelete() {
        service.undelete(id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeclinationReason() {
        return declinationReason;
    }

    public void setDeclinationReason(String declinationReason) {
        this.declinationReason = declinationReason;
    }
}
