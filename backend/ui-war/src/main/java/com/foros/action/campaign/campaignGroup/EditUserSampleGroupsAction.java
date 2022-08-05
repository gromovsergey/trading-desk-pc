package com.foros.action.campaign.campaignGroup;

import com.foros.framework.ReadOnly;

public class EditUserSampleGroupsAction extends UserSampleGroupsSupportAction {

    @ReadOnly
    public String edit() {
        group = groupService.findForUpdateUserSampleGroups(group.getId());
        return SUCCESS;
    }
}
