package com.foros.action.admin.wdRequestMapping;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.admin.WDRequestMapping;
import com.foros.session.admin.wdRequestMapping.WDRequestMappingService;

import java.util.List;
import javax.ejb.EJB;

public class ListWDRequestMappingAction extends BaseActionSupport {

    @EJB
    WDRequestMappingService service;

    private List<WDRequestMapping> entities;

    public List<WDRequestMapping> getEntities() {
        return entities;
    }

    @ReadOnly
    public String list() {
        entities = service.findAll();
        return SUCCESS;
    }
}
