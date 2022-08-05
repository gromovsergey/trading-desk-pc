package com.foros.action.action;

import com.foros.action.BaseActionSupport;
import com.foros.session.action.ActionService;
import com.foros.session.CurrentUserService;
import com.foros.util.AccountUtil;

import javax.ejb.EJB;

public class StatusActionAction extends BaseActionSupport {

    @EJB
    private ActionService actionService;

    @EJB
    private CurrentUserService currentUserService;

    private Long id;

    private Long advertiserId;

    public String delete() {
        actionService.delete(getId());
        advertiserId = actionService.findById(getId()).getAccount().getId();
        if (currentUserService.isExternal()) {
            return "successExternal";
        }
        return SUCCESS;
    }

    public String undelete() {
        actionService.undelete(getId());
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountParam(String parameterName) {
        return AccountUtil.getAccountParam(parameterName, advertiserId);
    }
}
