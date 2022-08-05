package com.foros.action.campaign.bulk;

public class SubmitUploadCampaignAction extends UploadCampaignActionSupport {

    public String submit() {
        bulkCampaignToolsService.createOrUpdateAll(getAdvertiserId(), validationResult.getId());
        setAlreadySubmitted(true);
        addActionMessage(getText("TextAd.upload.success"));
        return INPUT;
    }
}
