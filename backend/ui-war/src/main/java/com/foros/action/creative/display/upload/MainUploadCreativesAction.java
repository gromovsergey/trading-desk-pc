package com.foros.action.creative.display.upload;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

public class MainUploadCreativesAction extends BaseUploadCreativesAction {

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.update")
    public String main() {
        return SUCCESS;
    }
}
