package com.foros.action.user;

import com.foros.action.BaseActionSupport;
import com.foros.session.security.UserService;
import com.foros.util.MailHelper;
import com.foros.util.RequestUtil;

import javax.ejb.EJB;
import org.apache.struts2.ServletActionContext;

public class GetChangeUidAction extends BaseActionSupport {

    @EJB
    private UserService userService;

    private Long id;
    private String changePasswordUrl;


    @Override
    public String execute() {
        String uid = userService.createChangePasswordUid(id);
        changePasswordUrl = MailHelper.changePasswordUrl(uid, RequestUtil.getBaseUrl(ServletActionContext.getRequest()));
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChangePasswordUrl() {
        return changePasswordUrl;
    }
}
