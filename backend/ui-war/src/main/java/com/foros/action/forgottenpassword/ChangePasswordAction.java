package com.foros.action.forgottenpassword;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.Trim;
import com.foros.session.security.UserService;

import javax.ejb.EJB;

@Trim(exclude = {"password", "repeatedPassword"})
public class ChangePasswordAction extends BaseActionSupport {

    @EJB
    private UserService userService;

    private String password;
    private String repeatedPassword;
    private Long userCredentialId;
    private String uid;

    @Override
    @ReadOnly
    public String execute() {
        try {
            userService.changePasswordByUid(userCredentialId, uid, password, repeatedPassword);
            return SUCCESS;
        } catch (SecurityException e) {
            addActionError(getText("password.Assistance.error.cantChangePassword"));
            return INPUT;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    public Long getUserCredentialId() {
        return userCredentialId;
    }

    public void setUserCredentialId(Long userCredentialId) {
        this.userCredentialId = userCredentialId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
