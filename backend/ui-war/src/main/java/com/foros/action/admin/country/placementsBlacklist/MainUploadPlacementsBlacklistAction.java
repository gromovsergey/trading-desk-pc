package com.foros.action.admin.country.placementsBlacklist;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

public class MainUploadPlacementsBlacklistAction extends BaseUploadPlacementsBlacklistAction {

    @ReadOnly
    @Restrict(restriction = "PlacementsBlacklist.update")
    public String main() {
        return SUCCESS;
    }
}
