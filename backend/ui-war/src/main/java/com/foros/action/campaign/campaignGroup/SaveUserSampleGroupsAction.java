package com.foros.action.campaign.campaignGroup;

import com.foros.model.campaign.CampaignCreativeGroup;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "userSampleGroupStart", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "userSampleGroupEnd", key = "errors.field.integer") })
public class SaveUserSampleGroupsAction extends UserSampleGroupsSupportAction {

    public String update() {
        prepare();
        groupService.updateUserSampleGroups(group);
        return SUCCESS;
    }

    private void prepare() {
        CampaignCreativeGroup existingGroup = groupService.find(group.getId());
        existingGroup.setUserSampleGroupEnd(group.getUserSampleGroupEnd());
        existingGroup.setUserSampleGroupStart(group.getUserSampleGroupStart());
        existingGroup.setVersion(group.getVersion());
        group = existingGroup;
    }

    @Override
    public void validate() {
        if (hasErrors()) {
            group = groupService.find(group.getId());
        }
    }
}
