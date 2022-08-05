package com.foros.session.channel;

import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.trigger.TriggersHolder;
import com.foros.session.admin.categoryChannel.CategoryChannelValidations;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class BehavioralChannelValidations {

    @EJB
    private AdvertisingChannelValidations advertisingChannelValidations;

    @EJB
    private BehavioralParametersValidations behavioralParametersValidations;

    @EJB
    private BaseTriggerListValidations baseTriggerListValidations;

    @EJB
    private CategoryChannelValidations categoryValidations;

    @EJB
    private LanguageChannelValidations languageChannelValidations;

    @EJB
    private BehavioralChannelService behavioralChannelServiceBean;

    @Validation
    public void validateCreate(ValidationContext context, BehavioralChannel channel) {
        validate(context.subContext(channel)
                .withMode(ValidationMode.CREATE)
                .build(), channel, null);
    }

    @Validation
    public void validateUpdate(ValidationContext context, BehavioralChannel channel) {
        BehavioralChannel existing = behavioralChannelServiceBean.findWithTriggers(channel.getId());
        validate(context
                .subContext(channel)
                .withMode(ValidationMode.UPDATE)
                .build(),
                channel, existing);
    }

    public void validate(ValidationContext context,
                         BehavioralChannel channel, BehavioralChannel existing) {
        advertisingChannelValidations.validate(context, channel, existing);
        languageChannelValidations.validate(context, channel);

        if (existing != null && ChannelVisibility.CMP.equals(existing.getVisibility())) {
            if (context.isReachable("behavioralParameters")) {
                if (isChangedParams(existing, channel)) {
                    context.addConstraintViolation("errors.field.canNotChangeWithoutTrueValue")
                    .withPath("behavioralParameters");
                } else {
                    channel.setBehavioralParameters(null);
                    channel.unregisterChange("behavioralParameters");
                }
            }

            if (context.isReachable("urls") && !TriggersHolder.equals(existing.getUrls(), channel.getUrls())) {
                context.addConstraintViolation("errors.field.canNotChangeWithoutTrueValue")
                .withPath("urls");
            }

            if (context.isReachable("pageKeywords") && !TriggersHolder.equals(existing.getPageKeywords(), channel.getPageKeywords())) {
                context.addConstraintViolation("errors.field.canNotChangeWithoutTrueValue")
                        .withPath("pageKeywords");
            }

            if (context.isReachable("searchKeywords") && !TriggersHolder.equals(existing.getSearchKeywords(), channel.getSearchKeywords())) {
                context.addConstraintViolation("errors.field.canNotChangeWithoutTrueValue")
                        .withPath("searchKeywords");
            }

            if (context.isReachable("urlKeywords") && !TriggersHolder.equals(existing.getUrlKeywords(), channel.getUrlKeywords())) {
                context.addConstraintViolation("errors.field.canNotChangeWithoutTrueValue")
                        .withPath("urlKeywords");
            }
        }

        if (context.props("urls", "pageKeywords", "searchKeywords", "urlKeywords").reachableAndNoViolations()) {
            baseTriggerListValidations.validate(context, channel, existing);
        }

        if (context.props("behavioralParameters").reachableAndNoViolations()) {
            behavioralParametersValidations.validate(context, channel);
        }

        validateTriggersRequired(context, channel, existing);
        categoryValidations.validateCategories(context, channel);
    }

    private boolean isChangedParams(BehavioralChannel existing, BehavioralChannel channel) {
        Set<BehavioralParameters> parameters = new HashSet<BehavioralParameters>();
        for (BehavioralParameters behavioralParameters : existing.getBehavioralParameters()) {
            BehavioralParameters param = new BehavioralParameters();
            param.setMinimumVisits(behavioralParameters.getMinimumVisits());
            param.setTimeFrom(behavioralParameters.getTimeFrom());
            param.setTimeTo(behavioralParameters.getTimeTo());
            param.setTriggerType(behavioralParameters.getTriggerType());
            parameters.add(param);
        }

        return !parameters.equals(channel.getBehavioralParameters());
    }

    private void validateTriggersRequired(ValidationContext context, BehavioralChannel channel, BehavioralChannel existing) {

        if (!context.props("behavioralParameters", "urls", "pageKeywords", "searchKeywords", "urlKeywords").reachableAndNoViolations()) {
            return;
        }

        Collection<BehavioralParameters> effectiveParameters = context.isReachable("behavioralParameters") ?
                channel.getBehavioralParameters() : existing == null ? null : existing.getBehavioralParameters();

        baseTriggerListValidations.validateTriggersRequired(context, channel, existing, effectiveParameters);
    }

    @Validation
    public void validateSubmitToCmp(ValidationContext context, BehavioralChannel channel) {
        advertisingChannelValidations.validateSubmitToCmp(context, channel);
    }
}
