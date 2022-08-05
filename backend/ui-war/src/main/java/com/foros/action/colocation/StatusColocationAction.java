package com.foros.action.colocation;

import com.foros.action.BaseActionSupport;
import com.foros.session.colocation.ColocationService;

import javax.ejb.EJB;

public class StatusColocationAction extends BaseActionSupport {

    @EJB
    private ColocationService colocationService;

    // parameters
    private Long id;

    public String delete() {
        colocationService.delete(id);
        return SUCCESS;
    }

    public String undelete() {
        colocationService.undelete(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
