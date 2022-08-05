package com.foros.action.admin.applicationFormat;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.template.ApplicationFormat;
import com.foros.session.template.ApplicationFormatService;

import java.util.List;
import javax.ejb.EJB;

public class ListApplicationFormatAction extends BaseActionSupport {

    @EJB
    private ApplicationFormatService appFormatService;

    private List<ApplicationFormat> entities;

    public List<ApplicationFormat> getEntities() {
        return entities;
    }

    @ReadOnly
    public String list() {
        entities = appFormatService.findAll();
        return SUCCESS;
    }
}
