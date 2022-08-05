package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.model.FrequencyCap;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.campaign.ccg.bulk.SetFrequencyCapOperation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.List;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.periodSpan.value", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowLengthSpan.value", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowCount", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.lifeCount", key = "errors.field.integer")
        }
)
public class SaveBulkFrequencyCapsAction extends CcgEditBulkActionSupport {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("operation.(#path)", "groups[0]", "violation.message")
            .add("groups[(#index)](#path)", "addGroupError(groups[0])", "violation.message")
            .rules();

    private FrequencyCap frequencyCap = new FrequencyCap();

    public String save() {
        perform(new SetFrequencyCapOperation<CampaignCreativeGroup>(frequencyCap));
        return SUCCESS;
    }

    public FrequencyCap getFrequencyCap() {
        return frequencyCap;
    }

    public void setFrequencyCap(FrequencyCap frequencyCap) {
        this.frequencyCap = frequencyCap;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }
}
