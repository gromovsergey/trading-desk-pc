package com.foros.action.site;

import com.foros.security.principal.SecurityContext;

import javax.persistence.EntityNotFoundException;

public class StatusWDTagAction extends WDTagActionSupport {

    public String delete() {
        if (wdTag.getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }

        wdTag = wdTagService.view(wdTag.getId());

        wdTagService.delete(wdTag.getId());
        return SecurityContext.isInternal() ? "success-admin" : "success-publisher";
    }

    public String undelete() {
        if (wdTag.getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }

        wdTagService.undelete(wdTag.getId());
        return SUCCESS;
    }
}
