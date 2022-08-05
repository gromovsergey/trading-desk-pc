package com.foros.action.campaign.campaignGroup;

import com.foros.framework.ReadOnly;

public class EditTargetAction extends TargetSupportAction {

    private Long id;

    @ReadOnly
    public String edit() {
        group = groupService.findForUpdateTarget(id);
        searchCriteria.populateConditionOfVisibility(getExistingAccount());
        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
