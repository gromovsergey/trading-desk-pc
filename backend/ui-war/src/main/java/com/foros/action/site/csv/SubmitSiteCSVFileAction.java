package com.foros.action.site.csv;

import com.foros.session.site.SiteUploadValidationResultTO;
import com.foros.util.ExceptionUtil;

public class SubmitSiteCSVFileAction extends SiteUploadSupportAction {

    public String submitUpload() throws Exception{
        try {
            siteUploadService.createOrUpdateAll(getValidationResult().getId());
        } catch (NullPointerException npe) {
            return getInputResultName();
        } catch (Exception e) {
            handleError(ExceptionUtil.getRootException(e));
        }

        setUploadSubmitted(true);

        if (!getFieldErrors().isEmpty()) {
            return getInputResultName();
        } else {
            // clear validation result
            setValidationResult(new SiteUploadValidationResultTO());
            addActionMessage(getText("site.upload.success"));
        }

        return getSuccessResultName();
    }
}
