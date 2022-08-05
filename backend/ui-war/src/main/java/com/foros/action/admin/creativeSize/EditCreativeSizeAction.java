package com.foros.action.admin.creativeSize;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;

public class EditCreativeSizeAction extends CreativeSizeModelSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    public String create() {
        breadcrumbs = new Breadcrumbs().add(new CreativeSizesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        entity = service.findById(entity.getId());
        breadcrumbs = new Breadcrumbs().add(new CreativeSizesBreadcrumbsElement()).add(new CreativeSizeBreadcrumbsElement(entity)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    public String createCopy() {
        entity = service.createCopy(entity.getId());
        return SUCCESS;
    }

    public String changeSize() {
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
