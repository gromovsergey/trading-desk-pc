package com.foros.action.admin.applicationFormat;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.ApplicationFormat;
import com.foros.session.template.ApplicationFormatService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class ViewApplicationFormatAction extends BaseActionSupport implements ModelDriven<ApplicationFormat>, BreadcrumbsSupport {
    @EJB
    private ApplicationFormatService appFormatService;

    private Long id;

    private ApplicationFormat entity;

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public ApplicationFormat getModel() {
        return entity;
    }

    @ReadOnly
    public String view() {
        entity = appFormatService.findById(id);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new ApplicationFormatsBreadcrumbsElement()).add(new ApplicationFormatBreadcrumbsElement(entity));
    }
}
