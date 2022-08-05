package com.foros.action.colocation;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.isp.Colocation;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "colocationRate.revenueShareInPercent", key = "errors.field.number")
        }
)
public class SaveColocationAction extends ColocationActionSupport implements BreadcrumbsSupport {

    public String create() {
        colocation = colocationService.create(colocation);
        return SUCCESS;
    }

    public String update() {
        colocation = colocationService.update(colocation);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        if (colocation.getId() != null) {
            final Colocation persistent = colocationService.find(colocation.getId());
            return new Breadcrumbs().add(new ColocationBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        }
        return null;
    }
}
