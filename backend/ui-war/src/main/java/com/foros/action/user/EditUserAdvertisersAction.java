package com.foros.action.user;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.context.RequestContexts;

public class EditUserAdvertisersAction extends UserActionSupport implements RequestContextsAware {

    private Long id;

    @ReadOnly
    @Restrict(restriction = "User.updateAdvertisers", parameters = "find('User', #target.id)")
    public String edit() {
        user = userService.view(id);
        return SUCCESS;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(user.getAccount());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
