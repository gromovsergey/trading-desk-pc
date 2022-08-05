package com.foros.action.admin.wdFrequencyCaps;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName="eventsFrequencyCap.periodSpan.value", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="eventsFrequencyCap.windowLengthSpan.value", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="eventsFrequencyCap.windowCount", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="eventsFrequencyCap.lifeCount", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="categoriesFrequencyCap.periodSpan.value", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="categoriesFrequencyCap.windowLengthSpan.value", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="categoriesFrequencyCap.windowCount", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="categoriesFrequencyCap.lifeCount", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="channelsFrequencyCap.periodSpan.value", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="channelsFrequencyCap.windowLengthSpan.value", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="channelsFrequencyCap.windowCount", key="errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName="channelsFrequencyCap.lifeCount", key="errors.field.integer")
    }
)
public class SaveWDFrequencyCapsAction extends WDFrequencyCapsActionSupport implements BreadcrumbsSupport {

    @Validate(validation = "FrequencyCap.updateWDFrequencyCaps",
            parameters = {"#target.eventsFrequencyCap", "#target.categoriesFrequencyCap", "#target.channelsFrequencyCap"})
    public String save() {
        globalParamsService.updateWDFrequencyCaps(getEventsFrequencyCap(), getCategoriesFrequencyCap(), getChannelsFrequencyCap());
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new WDFrequencyCapsBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}
