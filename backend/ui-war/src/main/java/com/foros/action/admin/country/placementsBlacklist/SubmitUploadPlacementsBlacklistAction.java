package com.foros.action.admin.country.placementsBlacklist;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

import java.security.AccessControlException;

public class SubmitUploadPlacementsBlacklistAction extends BaseUploadPlacementsBlacklistAction {

    @ReadOnly
    @Restrict(restriction = "PlacementsBlacklist.update")
    public String submit() {
        if (validationResult == null || validationResult.getId() == null) {
            throw new AccessControlException("Validation result id is required");
        }
        placementsBlacklistService.createOrDropAll(validationResult.getId());
        setAlreadySubmitted(true);
        addActionMessage(getText("admin.placementsBlacklist.bulkUpload.success"));
        return INPUT;
    }
}
