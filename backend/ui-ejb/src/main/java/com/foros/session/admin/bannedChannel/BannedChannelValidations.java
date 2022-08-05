package com.foros.session.admin.bannedChannel;

import com.foros.model.channel.BannedChannel;
import com.foros.session.channel.BaseTriggerListValidations;
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
public class BannedChannelValidations {

    @EJB
    private BannedChannelService bannedChannelService;

    @EJB
    private BaseTriggerListValidations baseTriggerListValidations;
    
    @Validation
    public void validateUpdate(ValidationContext context, BannedChannel bannedChannel) {
        BannedChannel existing = bannedChannelService.findById(bannedChannel.getId());
        ValidationContext subContext = context
                .subContext(bannedChannel)
                .withMode(ValidationMode.UPDATE)
                .build();
        baseTriggerListValidations.validate(subContext, bannedChannel, existing);
    }

}
