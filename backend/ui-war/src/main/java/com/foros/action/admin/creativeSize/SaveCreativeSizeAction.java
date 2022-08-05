package com.foros.action.admin.creativeSize;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.creative.CreativeSize;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "height", key = "CreativeSize.error.height"),
        @ConversionErrorFieldValidator(fieldName = "width", key = "CreativeSize.error.width"),
        @ConversionErrorFieldValidator(fieldName = "maxHeight", key = "CreativeSize.error.maxHeight"),
        @ConversionErrorFieldValidator(fieldName = "maxWidth", key = "CreativeSize.error.maxWidth"),
        @ConversionErrorFieldValidator(fieldName="maxTextCreatives", key="errors.field.integer")
    }
)
public class SaveCreativeSizeAction extends CreativeSizeModelSupport implements BreadcrumbsSupport {

    @Validate(validation = "CreativeSize.create", parameters = "#target.model")
    public String create() {
        service.create(entity);
        return SUCCESS;
    }

    @Validate(validation = "CreativeSize.update", parameters = "#target.model")
    public String update() {
        entity.registerChange("expansions");
        service.update(entity);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (entity.getId() != null) {
            CreativeSize persistent = service.findById(entity.getId());
            breadcrumbs = new Breadcrumbs().add(new CreativeSizesBreadcrumbsElement()).add(new CreativeSizeBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new CreativeSizesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
