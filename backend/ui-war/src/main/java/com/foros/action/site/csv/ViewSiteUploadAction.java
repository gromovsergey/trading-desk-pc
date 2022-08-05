package com.foros.action.site.csv;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

public class ViewSiteUploadAction extends SiteUploadSupportAction {

    @ReadOnly
    @Restrict(restriction = "PublisherEntity.upload", parameters = "#target.publisherId")
    public String selectUpload() {
        return getSuccessResultName();
    }
}
