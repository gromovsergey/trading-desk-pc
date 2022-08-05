package com.foros.session.channel;

import com.foros.model.channel.Channel;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class CommonChannelValidations {

    @Validation
    public void validateChannelName(final ValidationContext context, Channel channel) {
        if (context.isReachable("name")) {
            validateChannelNameImpl(context, 100, channel.getName());
        }
    }

    protected void validateChannelNameImpl(final ValidationContext context, int size, final String value) {
        if (!isStringValid(size, value)) {
            context.addConstraintViolation("errors.field.maxlength")
                    .withPath("name")
                    .withParameters(size)
                    .withValue(value);

        }
    }

    private boolean isStringValid(int size, final String value) {
        return StringUtil.isPropertyEmpty(value) || value.length() <= size;

    }
}
