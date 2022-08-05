package com.foros.action.user;

import com.foros.action.BaseActionSupport;
import com.foros.framework.Trim;
import com.foros.model.security.User;
import com.foros.session.security.UserService;

import javax.ejb.EJB;

@Trim(exclude = {"oldPassword", "newPassword", "repeatedPassword"})
public class SaveMyPasswordAction extends BaseActionSupport {

    @EJB
    private UserService userService;

    private String oldPassword;

    private String newPassword;

    private String repeatedPassword;

    public String save() {
        User user = userService.getMyUser();
        userService.changePassword(user.getId(), oldPassword, newPassword, repeatedPassword);
        return SUCCESS;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }


    public String getNewPassword() {
        return newPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
    
    public String getRepeatedPassword() {
        return repeatedPassword;
    }

}
