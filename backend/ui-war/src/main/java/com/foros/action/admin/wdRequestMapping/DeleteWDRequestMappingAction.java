package com.foros.action.admin.wdRequestMapping;

import com.foros.action.BaseActionSupport;
import com.foros.session.admin.wdRequestMapping.WDRequestMappingService;

import javax.ejb.EJB;

public class DeleteWDRequestMappingAction extends BaseActionSupport {
    @EJB
    WDRequestMappingService service;

    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public String delete() {
        service.delete(id);
        return SUCCESS;
    }
}
