package com.foros.action.channel.bulk;

import com.foros.framework.support.RequestContextsAware;
import com.foros.model.channel.Channel;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.List;

public class ValidateUploadChannelAction extends ValidateUploadChannelBaseAction implements RequestContextsAware {

    @Validations(
            requiredFields = {
                    @RequiredFieldValidator(fieldName = "format", key = "errors.field.required"),
                    @RequiredFieldValidator(fieldName = "bulkFile", key = "errors.field.required"),
                    @RequiredFieldValidator(fieldName = "channelType", key = "errors.field.required")
            }
    )
    public String validateCsv() {
        List<Channel> channels = readBulk(false);

        for (Channel channel: channels) {
            channel.setAccount(getAccount());
        }
        validationResult = bulkChannelToolsService.validateAll(channelType, channels);

        return INPUT;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(getAccount());
    }
}
