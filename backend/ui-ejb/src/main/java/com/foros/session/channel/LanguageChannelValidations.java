package com.foros.session.channel;

import com.foros.model.channel.Channel;
import com.foros.model.channel.LanguageChannel;
import com.foros.session.channel.service.ChannelUtils;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
public class LanguageChannelValidations {

    public <T extends Channel & LanguageChannel> void validate(ValidationContext context, T channel) {
        if (!channel.isChanged("language")) {
            return;
        }

        String language = channel.getLanguage();

        if (StringUtil.isPropertyEmpty(language)) {
            context.addConstraintViolation("errors.field.required")
                   .withPath("language");
            return;
        }

        if (!ChannelUtils.getAvailableLanguages().contains(language)) {
            context.addConstraintViolation("channel.errors.language")
                   .withPath("language")
                   .withValue(language);
        }
    }

}
