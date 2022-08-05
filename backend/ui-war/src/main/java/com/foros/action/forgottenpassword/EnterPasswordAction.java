package com.foros.action.forgottenpassword;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.security.UserNotFoundException;
import com.foros.session.security.UserService;
import com.foros.util.StringUtil;

import javax.ejb.EJB;

public class EnterPasswordAction extends BaseActionSupport {

    @EJB
    private UserService userService;

    private String uid;
    private Long userCredentialId;

    @Override
    @ReadOnly
    public String execute() {
        try {
            uid = StringUtil.isPropertyEmpty(uid) ? "" : uid;
            userCredentialId = userService.findByChangePasswordUid(uid).getId();
            return SUCCESS;
        } catch (UserNotFoundException e) {
            addActionError(getText("password.Assistance.error.noUidDefined"));
            return INPUT;
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getUserCredentialId() {
        return userCredentialId;
    }

    public void setUserCredentialId(Long userCredentialId) {
        this.userCredentialId = userCredentialId;
    }

}
