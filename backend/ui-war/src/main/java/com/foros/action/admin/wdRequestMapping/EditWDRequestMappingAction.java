package com.foros.action.admin.wdRequestMapping;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.admin.WDRequestMapping;
import com.foros.session.admin.wdRequestMapping.WDRequestMappingService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class EditWDRequestMappingAction extends BaseActionSupport implements ModelDriven<WDRequestMapping>, BreadcrumbsSupport {

    @EJB
    WDRequestMappingService service;

    private Long id;

    private WDRequestMapping entity;

    private Breadcrumbs breadcrumbs;

    public void setId(Long id) {
        this.id = id;
    }

    public WDRequestMapping getModel() {
        return entity;
    }

    @ReadOnly
    public String create() {
        breadcrumbs = new Breadcrumbs().add(new WDRequestMappingsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        entity = service.findById(id);
        breadcrumbs = new Breadcrumbs().add(new WDRequestMappingsBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
