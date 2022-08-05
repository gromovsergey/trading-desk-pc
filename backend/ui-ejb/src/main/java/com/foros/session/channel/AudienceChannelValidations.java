package com.foros.session.channel;

import com.foros.model.channel.AudienceChannel;
import com.foros.session.channel.service.AudienceChannelService;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@LocalBean
@Stateless
@Validations
public class AudienceChannelValidations {

    @EJB
    private AdvertisingChannelValidations advertisingChannelValidations;

    @EJB
    private AudienceChannelService audienceChannelService;

    @Validation
    public void validateCreate(ValidationContext context, AudienceChannel channel) {
        advertisingChannelValidations.validate(context.subContext(channel).withMode(ValidationMode.CREATE).build(), channel, null);
    }

    @Validation
    public void validateUpdate(ValidationContext context, AudienceChannel channel) {
        AudienceChannel existing = audienceChannelService.find(channel.getId());
        advertisingChannelValidations.validate(context.subContext(channel).withMode(ValidationMode.UPDATE).build(), channel, existing);
    }
}
