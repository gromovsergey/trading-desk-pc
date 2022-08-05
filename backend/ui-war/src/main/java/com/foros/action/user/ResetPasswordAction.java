package com.foros.action.user;

import com.foros.action.BaseActionSupport;
import com.foros.model.security.User;
import com.foros.session.security.UserService;
import com.foros.util.MailHelper;
import com.foros.util.RequestUtil;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

public class ResetPasswordAction extends BaseActionSupport implements ServletRequestAware{

    @EJB
    private UserService userService;

    private Long id;

    private HttpServletRequest request;

    private boolean passwordSent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPasswordSent() {
        return passwordSent;
    }

    @Override
    public String execute(){
        String newPassword = userService.generatePassword(UserService.GENPASSWORD_LENGTH);
        User user = userService.find(Long.valueOf(id));
        user.setNewPassword(userService.hashPassword(newPassword));

        if (userService.resetPassword(user)) {
            MailHelper.sendNewPasswordMail(user, newPassword, RequestUtil.getBaseUrl(request));
        }
        passwordSent = user.isMailSent();
        return SUCCESS;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
