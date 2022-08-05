package com.foros.action.admin.creativeCategories;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;

public class EditCreativeCategoryAction extends SupportCreativeCategoryAction implements BreadcrumbsSupport {

    @ReadOnly
    public String edit() {
        setEditTO(creativeCategoryService.getForEdit(getType()));
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CreativeCategoriesBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}
