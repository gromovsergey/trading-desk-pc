package com.foros.action.channel.bulk;

import com.foros.action.channel.ChannelsBreadcrumbsElement;
import com.foros.action.channel.UploadChannelsBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.Channel;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.List;


public class ValidateUploadChannelInternalAction extends ValidateUploadChannelBaseAction implements BreadcrumbsSupport {

    @Validations(
            requiredFields = {
                    @RequiredFieldValidator(fieldName = "format", key = "errors.field.required"),
                    @RequiredFieldValidator(fieldName = "bulkFile", key = "errors.field.required"),
                    @RequiredFieldValidator(fieldName = "channelType", key = "errors.field.required")
            }
    )
    public String validateCsv() {
        List<Channel> channels = readBulk(true);
        validationResult = bulkChannelToolsService.validateAll(channelType, channels);
        return INPUT;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(new UploadChannelsBreadcrumbsElement());
    }
}
