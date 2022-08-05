package com.foros.action.creative.display;

import com.foros.action.BaseActionSupport;
import com.foros.security.principal.SecurityContext;
import com.foros.session.creative.DisplayCreativeService;

import javax.ejb.EJB;

public class UpdateCreativeStatusAction extends BaseActionSupport {

    @EJB
    private DisplayCreativeService displayCreativeService;

    private Long id;

    private String declinationReason;

    private Long advertiserId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setDeclinationReason(String declinationReason) {
        this.declinationReason = declinationReason;
    }

    public String activate() {
        displayCreativeService.activate(id);
        return SUCCESS;
    }

    public String inactivate() {
        displayCreativeService.inactivate(id);
        return SUCCESS;
    }

    public String delete() {
        if (!SecurityContext.isInternal()) {
            advertiserId = displayCreativeService.find(id).getAccount().getId();
        }
        displayCreativeService.delete(id);
        return SUCCESS;
    }

    public String undelete() {
        displayCreativeService.undelete(id);
        return SUCCESS;
    }

    public String approve() {
        displayCreativeService.approve(id);
        return SUCCESS;
    }

    public String decline() {
        displayCreativeService.decline(id, declinationReason);
        return SUCCESS;
    }
}
