package com.foros.action.creative.display.upload;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

import java.security.AccessControlException;

public class SubmitUploadCreativesAction extends BaseUploadCreativesAction {

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "#target.account")
    public String submit() {
        if (validationResult == null || validationResult.getId() == null) {
            throw new AccessControlException("Validation result id is required");
        }
        displayCreativeService.createOrUpdateAll(validationResult.getId());
        setAlreadySubmitted(true);
        addActionMessage(getText("creative.upload.success"));
        return INPUT;
    }
}
