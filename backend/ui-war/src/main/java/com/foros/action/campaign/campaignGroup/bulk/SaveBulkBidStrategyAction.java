package com.foros.action.campaign.campaignGroup.bulk;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.foros.session.campaign.ccg.bulk.SetBidStrategyOperation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.List;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "minCtrGoal", key = "errors.field.number")
        }
)
public class SaveBulkBidStrategyAction extends BulkBidStrategyActionSupport {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("operation.(#path)", "'minCtrGoal'", "violation.message")
            .rules();

    public String save() {
        perform(new SetBidStrategyOperation(getBidStrategy(), getMinCtrGoal()));
        return SUCCESS;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }
}