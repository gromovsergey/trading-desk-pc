package com.foros.action.admin.userRole;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.security.UserRole;
import com.foros.session.NamedTO;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.Converter;

public class SaveUserRoleAction extends UserRoleActionSupport implements BreadcrumbsSupport {

    public String create() {
        prepare();
        service.create(getModel());
        return SUCCESS;
    }

    public String update() {
        prepare();
        service.update(getModel());
        return SUCCESS;
    }
    
    private void prepare() {
        getModel().getAccessAccountIds().addAll(CollectionUtils.convert(new Converter<NamedTO, Long>() {
            @Override
            public Long item(NamedTO value) {
                return value.getId();
            }
        }, selectedAccountIdList));
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (entity.getId() != null) {
            UserRole persistent = service.findById(entity.getId());
            breadcrumbs = new Breadcrumbs().add(new UserRolesBreadcrumbsElement()).add(new UserRoleBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new UserRolesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
