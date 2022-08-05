package com.foros.action.context;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.security.UserService;
import com.foros.util.ContextUrlHelper;
import com.foros.util.StringUtil;

import javax.ejb.EJB;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;

public class SwitchContextErrorAction extends BaseActionSupport {

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private UserService userService;

    private Collection<User> users;

    private String requestedURL;

    @ReadOnly
    public String list() {
        Set<AccountRole> requestedRoles = ContextUrlHelper.getContextAccountRoles(requestedURL);
        users = userService.findSwitchableUsersForRole(currentUserService.getUserId(), requestedRoles);

        // sort users by account name
        Collections.sort((List<User>)users, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return StringUtil.lexicalCompare(user1.getAccount().getName(), user2.getAccount().getName());
            }
        });
        
        return SUCCESS;
    }

    public void setRequestedURL(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    public String getRequestedURL() {
        return requestedURL;
    }

    public Collection<User> getUsers() {
        return users;
    }
}
