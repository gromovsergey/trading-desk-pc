package com.foros.action.admin.wdRequestMapping;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.admin.WDRequestMapping;
import com.foros.session.admin.wdRequestMapping.WDRequestMappingService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class SaveWDRequestMappingAction extends BaseActionSupport implements ModelDriven<WDRequestMapping>, BreadcrumbsSupport {
    @EJB
    WDRequestMappingService service;

    private WDRequestMapping entity = new WDRequestMapping();

    public WDRequestMapping getModel() {
        return entity;
    }

    public String create() {
        service.create(entity);
        return SUCCESS;
    }

    public String update() {
        service.update(entity);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        if (entity.getId() != null) {
            return new Breadcrumbs().add(new WDRequestMappingsBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
        } else {
            return new Breadcrumbs().add(new WDRequestMappingsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }
    }
}
