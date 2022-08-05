package com.foros.session.channel;

import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersChannel;
import com.foros.model.channel.BehavioralParametersUnits;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@LocalBean
@Stateless
@Validations
public class BehavioralParametersValidations {

    @EJB
    private BeansValidationService beanValidationService;

    public void validate(ValidationContext context, BehavioralParametersChannel channel) {
        if (!context.isReachable("behavioralParameters")) {
            return;
        }
        Collection<BehavioralParameters> behavioralParameters = channel.getBehavioralParameters();

        Set<Character> triggerTypes = new HashSet<Character>();
        for (BehavioralParameters params : behavioralParameters) {
            if (!triggerTypes.add(params.getTriggerType())) {
                context
                        .addConstraintViolation("errors.behavioralParameters.duplicateTriggerType")
                        .withPath("behavioralParameters");
            }
        }

        for (BehavioralParameters params : behavioralParameters) {
            ValidationContext subContext = context.createSubContext(params, "behavioralParameters[" + params.getTriggerType() + "]");
            validate(subContext, params);
        }
    }

    public void validate(ValidationContext context, BehavioralParameters params) {
        beanValidationService.validate(context);

        if (!context.props("searchKeywordUnit", "pageKeywordUnit", "urlKeywordUnit", "urlUnit").all().reachableAndNoViolations()) {
            return;
        }

        if (context.props("triggerType", "timeFrom", "timeTo", "minimumVisits").all().reachableAndNoViolations()) {
            BehavioralParametersUnits timeUnit =
                    BehavioralParametersUnits.calculate(params.getTimeTo(), params.getTimeFrom());
            if (timeUnit == null) {
                context.addConstraintViolation("errors.behavioralParameters.timeInterval.invalid");
                return;
            }
            if (params.getTimeFrom() > params.getTimeTo()
                    || params.getTimeFrom().equals(params.getTimeTo()) && params.getTimeFrom() != 0) {
                context.addConstraintViolation("errors.behavioralParameters.timeInterval.fromShouldBeLessThanTo");
            }
            if (params.getTimeFrom() >= timeUnit.getMaxValue()) {
                context.addConstraintViolation("errors.behavioralParameters.timeUnit.notgreater")
                        .withParameters(timeUnit.getMaxValue() / timeUnit.getMultiplier() - 1,
                                StringUtil.getLocalizedString("channel.params." + timeUnit.getName()))
                        .withPath("timeFrom");
            }
            if (params.getTimeTo() > timeUnit.getMaxValue()) {
                context.addConstraintViolation("errors.behavioralParameters.timeUnit.notgreater")
                        .withParameters(timeUnit.getMaxValue() / timeUnit.getMultiplier(),
                                StringUtil.getLocalizedString("channel.params." + timeUnit.getName()))
                        .withPath("timeTo");
            }
            if (params.getMinimumVisits() > 1 && params.getTimeFrom() == 0 && params.getTimeTo() == 0) {
                context.addConstraintViolation("errors.behavioralParameters.minimumVisits.invalid").withPath("minimumVisits");
            }
        }
        if (context.props("triggerType", "timeTo").all().reachableAndNoViolations()) {
            if (params.getTimeTo() < 0) {
                context.addConstraintViolation("errors.field.less")
                        .withParameters(0).withPath("timeTo");
            }
        }
    }
}
