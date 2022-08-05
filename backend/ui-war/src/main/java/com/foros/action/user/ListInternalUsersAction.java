package com.foros.action.user;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.security.AccountRole;
import com.foros.session.security.UserService;
import com.foros.session.security.UserTO;

import java.util.Collection;
import javax.ejb.EJB;

public class ListInternalUsersAction extends BaseActionSupport {

    @EJB
    private UserService userService;

    private Collection<UserTO> users;

    public Collection<UserTO> getUsers() {
        if (users == null) {
            users = userService.findByRole(AccountRole.INTERNAL);
        }
        return users;
    }

    @ReadOnly
    public String list() {
        return SUCCESS;

    }
}
