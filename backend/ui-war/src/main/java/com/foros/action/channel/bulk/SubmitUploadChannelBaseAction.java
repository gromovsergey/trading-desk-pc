package com.foros.action.channel.bulk;

public class SubmitUploadChannelBaseAction extends UploadChannelActionSupport {

    protected String doSubmit() {
        bulkChannelToolsService.createOrUpdateAll(validationResult.getChannelType(), validationResult.getId());
        setAlreadySubmitted(true);
        addActionMessage(getText("channel.upload.success"));
        setChannelType(validationResult.getChannelType());
        return INPUT;
    }
}
