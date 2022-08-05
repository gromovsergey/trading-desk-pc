package com.foros.action.admin.applicationFormat;

import com.foros.action.BaseActionSupport;
    import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.ApplicationFormat;
import com.foros.session.template.ApplicationFormatService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class EditApplicationFormatAction extends BaseActionSupport implements ModelDriven<ApplicationFormat>, BreadcrumbsSupport {

    private Long id;

    @EJB
    private ApplicationFormatService entityService;

    private ApplicationFormat entity;

    private Breadcrumbs breadcrumbs = new Breadcrumbs().add(new ApplicationFormatsBreadcrumbsElement());

    @Override
    public ApplicationFormat getModel() {
        return entity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ReadOnly
    public String create() {
        breadcrumbs.add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        entity = entityService.findById(id);
        breadcrumbs.add(new ApplicationFormatBreadcrumbsElement(entity)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
