package com.foros.action.user;

import com.foros.framework.ReadOnly;

public class ViewMyPreferencesAction extends UserActionSupport {
    @ReadOnly
    public String view() {
        user = userService.getMyUser();
        return SUCCESS;
    }
}
