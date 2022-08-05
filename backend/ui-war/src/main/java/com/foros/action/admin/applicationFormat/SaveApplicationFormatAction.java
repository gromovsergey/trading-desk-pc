package com.foros.action.admin.applicationFormat;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.ApplicationFormat;
import com.foros.session.template.ApplicationFormatService;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class SaveApplicationFormatAction extends BaseActionSupport implements ModelDriven<ApplicationFormat>, BreadcrumbsSupport {

    @EJB
    private ApplicationFormatService entityService;

    private ApplicationFormat entity = new ApplicationFormat();

    @Validate(validation = "ApplicationFormat.create", parameters = "#target.model")
    public String create() {
        entityService.create(entity);
        return SUCCESS;
    }

    @Validate(validation = "ApplicationFormat.update", parameters = "#target.model")
    public String update() {
        entityService.update(entity);
        return SUCCESS;
    }

    @Override
    public ApplicationFormat getModel() {
        return entity;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs().add(new ApplicationFormatsBreadcrumbsElement());
        if (entity.getId() != null) {
            ApplicationFormat persistent = entityService.findById(entity.getId());
            breadcrumbs.add(new ApplicationFormatBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs.add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
