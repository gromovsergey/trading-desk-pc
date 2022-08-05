package com.foros.action.admin.creativeSize;

import com.foros.action.BaseActionSupport;
import com.foros.session.creative.CreativeSizeService;

import javax.ejb.EJB;

public class StatusCreativeSizeAction extends BaseActionSupport {

    @EJB
    private CreativeSizeService service;

    private Long id;

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

}
