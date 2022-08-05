package com.foros.action.admin.creativeSize;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;

public class ViewSizeTypeAction extends SizeTypeModelSupport {
    private Long id;

    @ReadOnly
    public String view() {
        sizeType = sizeTypeService.view(id);
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
        return new Breadcrumbs()
                .add(new SizeTypesBreadcrumbsElement())
                .add(new SizeTypeBreadcrumbsElement(sizeType));
    }
}
