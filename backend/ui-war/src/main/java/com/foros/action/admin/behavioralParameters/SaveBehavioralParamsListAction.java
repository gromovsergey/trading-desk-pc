package com.foros.action.admin.behavioralParameters;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.Collections;

@Validations(
        conversionErrorFields = @ConversionErrorFieldValidator(fieldName = "threshold", key = "errors.field.integer"),
        customValidators = {
                @CustomValidator(type = "convtransform", key = "errors.field.number",
                        parameters = {@ValidationParameter(name = "fieldMask", value = "behavioralParameters\\[.+\\]\\.minimumVisits")}),
                @CustomValidator(type = "convtransform", key = "errors.field.number",
                        parameters = {@ValidationParameter(name = "fieldMask", value = "behavioralParameters\\[.+\\]\\.weight")})
        }
)
public class SaveBehavioralParamsListAction extends BehavioralParamsListActionSupport implements BreadcrumbsSupport {

    @Validate(validation = "BehavioralParamsList.update", parameters = "#target.model")
    public String update() {
        prepareModel();
        behavioralParamsListService.update(list);
        return SUCCESS;
    }

    @Validate(validation = "BehavioralParamsList.create", parameters = "#target.model")
    public String create() {
        prepareModel();
        behavioralParamsListService.create(list);
        return SUCCESS;
    }

    public void prepareModel() {
        // remove null objects
        list.getBehavioralParameters().removeAll(Collections.singletonList(null));
        // assign list object to parameters
        for (BehavioralParameters behavioralParameters : list.getBehavioralParameters()) {
            behavioralParameters.setParamsList(list);
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (list.getId() != null) {
            BehavioralParametersList persistent = behavioralParamsListService.find(list.getId());
            breadcrumbs = new Breadcrumbs().add(new BehavioralParametersBreadcrumbsElement()).add(new BehavioralParameterBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new BehavioralParametersBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
