package com.foros.action.admin.creativeSize;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.model.creative.SizeType;
import com.foros.restriction.annotation.Restrict;

public class EditSizeTypeAction extends SizeTypeModelSupport {

    private Long id;

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction = "CreativeSize.create")
    public String create() {
        sizeType = new SizeType();
        breadcrumbs = new Breadcrumbs().add(new SizeTypesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "CreativeSize.update")
    public String edit() {
        sizeType = sizeTypeService.view(id);
        breadcrumbs = new Breadcrumbs().add(new SizeTypesBreadcrumbsElement()).add(new SizeTypeBreadcrumbsElement(sizeType)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
