package com.foros.session.campaign.bulk;

import com.foros.session.channel.service.DeviceChannelSelector;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class ChannelSelectorValidations {

    @Validation
    public void validateAdvertising(ValidationContext context, @ValidateBean ChannelSelector selector) {
    }

    @Validation
    public void validateDiscover(ValidationContext context, @ValidateBean DiscoverChannelSelector selector) {
    }

    @Validation
    public void validateGeo(ValidationContext context, @ValidateBean GeoChannelSelector selector) {
    }

    @Validation
    public void validateDevice(ValidationContext context, @ValidateBean DeviceChannelSelector selector) {
    }
}
