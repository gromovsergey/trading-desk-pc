package com.foros.action.user;

import com.foros.action.BaseActionSupport;
import com.foros.session.security.UserService;
import javax.ejb.EJB;

public class StatusUserAction extends BaseActionSupport {

    @EJB
    private UserService userService;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String activate() {
        userService.activate(id);
        return SUCCESS;
    }

    public String inactivate() {
        userService.inactivate(id);
        return SUCCESS;
    }

    public String delete() {
        userService.delete(id);
        return SUCCESS;
    }

    public String undelete() {
        userService.undelete(id);
        return SUCCESS;
    }
}
