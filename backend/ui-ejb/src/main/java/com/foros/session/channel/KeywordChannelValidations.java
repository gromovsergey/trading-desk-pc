package com.foros.session.channel;

import com.foros.model.Country;
import com.foros.model.FrequencyCap;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.Channel;
import com.foros.model.channel.KeywordChannel;
import com.foros.session.channel.service.DefaultKeywordSettingsTO;
import com.foros.session.channel.service.KeywordChannelService;
import com.foros.util.UploadUtils;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;

import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.apache.commons.lang.StringUtils;

@LocalBean
@Stateless
@Validations
public class KeywordChannelValidations extends CommonChannelValidations {

    @EJB
    private ValidationService validationService;

    @EJB
    private BeansValidationService beanValidationService;

    @EJB
    private BehavioralParametersValidations behavioralParametersValidations;

    @EJB
    private KeywordChannelService keywordChannelService;

    @Validation
    public void validateUpdate(ValidationContext context, KeywordChannel channel) {
        KeywordChannel existing = null;

        if (channel.getId() != null) {
            existing = keywordChannelService.findById(channel.getId());
        }

        if (context.isReachable("triggerType") && existing != null) {
            if (!existing.getTriggerType().equals(channel.getTriggerType())) {
                context.addConstraintViolation("errors.keywordChannel.cantChangeTriggerType").withPath("triggerType");
            }
        }

        if (context.isReachable("behavioralParameters")) {
            Set<BehavioralParameters> parametersSet = channel.getBehavioralParameters();
            if (parametersSet == null || parametersSet.size() != 1) {
                context.addConstraintViolation("errors.keywordChannel.wrongParamsCount")
                        .withPath("behavioralParameters");
            } else {
                BehavioralParameters bp = parametersSet.iterator().next();
                ValidationContext subContext = context.createSubContext(bp, "behavioralParameters[" + bp.getTriggerType() + "]");
                behavioralParametersValidations.validate(subContext, bp);
                if (bp.getTriggerType() != (existing == null ? channel : existing).getTriggerType().getLetter()) {
                    subContext.addConstraintViolation("errors.keywordChannel.wrongParamsType")
                            .withPath("behavioralParameters");
                }
            }
        }

        validateChannelName(context, channel);

        if (context.isReachable("frequencyCap")) {
            FrequencyCap frequencyCap = channel.getFrequencyCap();
            if (frequencyCap != null) {
                ValidationContext frequencyCapContext = context.createSubContext(frequencyCap, "frequencyCap");
                validationService.validateWithContext(frequencyCapContext, "FrequencyCap.update", frequencyCap);
            }
        }
    }

    @Validation
    public void validateDefaultParameters(ValidationContext context, DefaultKeywordSettingsTO channel) {
        if (!context.isReachable("behavioralParameters")) {
            return;
        }
        Set<BehavioralParameters> parametersSet = channel.getBehavioralParameters();

        if (parametersSet == null || parametersSet.isEmpty()) {
            context.addConstraintViolation("errors.keywordChannel.noParams").withPath("behavioralParameters");
            return;
        }

        boolean pageParam = false;
        boolean searchParam = false;
        for (BehavioralParameters param: parametersSet) {
            pageParam = pageParam || param.isPageTriggerType();
            searchParam = searchParam || param.isSearchTriggerType();
        }

        if (!pageParam || !searchParam) {
            context.addConstraintViolation("errors.field.invalid").withPath("behavioralParameters");
            return;
        }

        behavioralParametersValidations.validate(context, channel);
    }

    @Validation
    public void validateUpdateAll(ValidationContext context, List<KeywordChannel> channels) {
        int index = 0;
        for (KeywordChannel channel : channels) {
            ValidationContext subContext = context.createSubContext(channel, "channels", index++);
            if(channel.getAccount()==null || StringUtils.isEmpty(channel.getAccount().getName())) {
                subContext.addConstraintViolation("errors.field.required").withPath("account");
            } else {
                Country country = channel.getAccount().getCountry();
                if(country==null || StringUtils.isEmpty(country.getCountryCode())) {
                    subContext.addConstraintViolation("errors.field.required").withPath("account.country");
                }
            }
            validationService.validateWithContext(subContext, "KeywordChannel.update", channel);
        }
    }

    @Validation
    public void validateAtBulk(final ValidationContext context, KeywordChannel existing, KeywordChannel channel) {
        if (existing == null) {
            ValidationContext subContext = context.createSubContext(channel, "channels", (int)(UploadUtils.getRowNumber(channel) - 1));
            subContext.addConstraintViolation("errors.keywordChannel.channelNotFound")
                .withPath("channel");
        }
    }

    @Override
    @Validation
    public void validateChannelName(ValidationContext context, Channel channel) {
        if (context.isReachable("name")) {
            validateChannelNameImpl(context, 512, channel.getName());
        }
    }
}
