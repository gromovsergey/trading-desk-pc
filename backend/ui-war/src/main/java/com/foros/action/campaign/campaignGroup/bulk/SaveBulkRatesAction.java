package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.model.campaign.CcgRate;
import com.foros.session.campaign.ccg.bulk.SetRateOperation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.List;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "rateValue", key = "errors.field.number")
        }
)
public class SaveBulkRatesAction extends BulkRatesActionSupport {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("operation.(#path)", "'rate'", "violation.message")
            .add("groups[(#index)](#path)", "addGroupError(groups[0])", "violation.message")
            .rules();

    public String save() {
        CcgRate rate = new CcgRate();
        rate.setRate(rateValue, rateType);
        perform(new SetRateOperation(rate));
        return SUCCESS;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }
}
