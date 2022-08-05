package com.foros.action.admin.fraudConditions;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.admin.FraudCondition;
import com.foros.model.admin.GlobalParam;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.List;

@Validations(
        conversionErrorFields = @ConversionErrorFieldValidator(fieldName = "userInactivityTimeout", key = "errors.field.integer"),
        customValidators = {
                @CustomValidator(type = "convtransform", key = "errors.field.integer",
                        parameters = {@ValidationParameter(name = "fieldMask", value = "fraudConditions\\[.+\\]\\.period")}),
                @CustomValidator(type = "convtransform", key = "errors.field.integer",
                        parameters = {@ValidationParameter(name = "fieldMask", value = "fraudConditions\\[.+\\]\\.limit")})
        }
)
public class SaveFraudConditionAction extends FraudConditionActionSupport implements BreadcrumbsSupport {
    @Validate(validation = "FraudCondition.update", parameters = { "#target.prepareUserInactivityTimeout()", "#target.prepareFraudConditions()" })
    public String save() {
        fraudConditionsService.update(getModel().getUpdatedUserInactivityTimeout(),
                                      getModel().getUpdatedFraudConditions());
        return SUCCESS;
    }

    public GlobalParam prepareUserInactivityTimeout() {
        return getModel().getUpdatedUserInactivityTimeout();
    }

    public List<FraudCondition> prepareFraudConditions() {
        return getModel().getUpdatedFraudConditions();
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new FraudConditionBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}
