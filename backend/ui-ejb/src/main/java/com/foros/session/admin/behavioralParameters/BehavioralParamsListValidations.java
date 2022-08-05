package com.foros.session.admin.behavioralParameters;

import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.session.channel.BehavioralParametersValidations;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import com.foros.validation.strategy.ValidationMode;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class BehavioralParamsListValidations {

    @EJB
    private BehavioralParametersValidations behavioralParametersValidations;

    @EJB
    private BehavioralParamsListService behavioralParametersListService;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) BehavioralParametersList behavioralParametersList) {
        validateBehavioralParamsList(context, behavioralParametersList.getBehavioralParameters());
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) BehavioralParametersList behavioralParametersList) {
        validateBehavioralParamsList(context, behavioralParametersList.getBehavioralParameters());
    }

    private void validateBehavioralParamsList(ValidationContext context, List<BehavioralParameters> behavioralParameters) {
        if (behavioralParameters.isEmpty()) {
            context
                    .addConstraintViolation("errors.field.required")
                    .withPath("params");
            return;
        }
        for (int i = 0; i < behavioralParameters.size(); i++) {
            BehavioralParameters params = behavioralParameters.get(i);
            if (params == null) {
                continue;
            }
            ValidationContext subContext = context.createSubContext(params, "behavioralParameters[" + i + "]");
            behavioralParametersValidations.validate(subContext, params);
        }
    }

    @Validation
    public void validateDelete(ValidationContext context, Long id) {
        if (behavioralParametersListService.getChannelUsageCount(id) > 0) {
            context
                    .addConstraintViolation("channel.params.list.inuse")
                    .withPath("error");
            return;
        }
    }
}
