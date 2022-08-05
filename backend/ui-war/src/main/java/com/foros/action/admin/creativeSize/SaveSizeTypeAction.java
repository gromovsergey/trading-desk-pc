package com.foros.action.admin.creativeSize;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.creative.SizeType;

public class SaveSizeTypeAction extends SizeTypeModelSupport {

    public String update() {
        sizeTypeService.update(sizeType);
        return SUCCESS;
    }

    public String create() {
        sizeTypeService.create(sizeType);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (sizeType.getId() != null) {
            SizeType persistent = sizeTypeService.find(sizeType.getId());
            breadcrumbs = new Breadcrumbs().add(new SizeTypesBreadcrumbsElement()).add(new SizeTypeBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new SizeTypesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }

}
