package com.foros.action.forgottenpassword;

import com.foros.action.BaseActionSupport;
import com.foros.config.ConfigService;
import com.foros.framework.ReadOnly;
import com.foros.model.security.User;
import com.foros.session.MailSendingFailedException;
import com.foros.session.security.UserService;
import com.foros.util.KaptchaUtils;
import com.foros.util.MailHelper;
import com.foros.util.RequestUtil;
import com.foros.validation.ValidationService;

import javax.ejb.EJB;
import org.apache.struts2.ServletActionContext;

public class SendChangeUidAction extends BaseActionSupport {

    @EJB
    private UserService userService;

    private String userEmail;
    private String captcha;

    @EJB
    private ValidationService validationService;

    @EJB
    private ConfigService configService;

    @Override
    public void validate() {
        if (hasErrors()) {
            captcha = "";
        }
    }

    @Override
    @ReadOnly
    public String execute() {
        try {
            String expectedCaptcha = KaptchaUtils.read(ServletActionContext.getRequest().getSession());
            validationService.validate("User.emailCaptcha", captcha, expectedCaptcha, userEmail).throwIfHasViolations();

            // find user by email
            User user = userService.findByEmail(userEmail);

            // create new password
            String uid = userService.createChangePasswordUid(userEmail);

            MailHelper.sendInstructionsForChangePassword(user, uid, RequestUtil.getBaseUrl(ServletActionContext.getRequest()));

            return SUCCESS;
        } catch (MailSendingFailedException e) {
            addActionError(getText("password.Assistance.error.cantSendMail"));
        }

        captcha = "";
        return INPUT;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
