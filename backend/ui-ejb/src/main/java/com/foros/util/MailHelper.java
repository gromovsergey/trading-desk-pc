package com.foros.util;

import com.foros.model.account.ExternalAccount;
import com.foros.model.security.User;
import com.foros.session.MailSendingFailedException;
import com.foros.session.MailService;
import com.foros.session.ServiceLocator;
import com.foros.util.templates.MailTemplate;
import com.foros.util.templates.Templates;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

public final class MailHelper {
    private MailHelper() {
    }

    public static void sendWelcomeMail(User user, String password, String forosBaseUrl) throws MailSendingFailedException {
        try {
            String messageKey = (StringUtils.isNotEmpty(password)) ? "user.mail.welcome.templateWithoutAccountManager" : "user.mail.welcome.templateWithoutAccountManager.withoutPassword";
            if (user.getAccount() instanceof ExternalAccount) {
                if (((ExternalAccount) user.getAccount()).getAccountManager() != null) {
                    messageKey = (StringUtils.isNotEmpty(password)) ? "user.mail.welcome.template" : "user.mail.welcome.template.withoutPassword";
                }
            }
            sendMail(user, password, "user.mail.welcome.subject", messageKey, forosBaseUrl);
            user.setMailSent(true);
        } catch (MailSendingFailedException e) {
            user.setMailSent(false);
        }
    }

    public static void sendNewPasswordMail(User user, String password, String forosBaseUrl) {
        try {
            String messageKey = "user.mail.newpassword.templateWithoutAccountManager";
            if (user.getAccount() instanceof ExternalAccount) {
                if (((ExternalAccount) user.getAccount()).getAccountManager() != null) {
                    messageKey = "user.mail.newpassword.template";
                }
            }

            sendMail(user, password, "user.mail.newpassword.subject", messageKey, forosBaseUrl);
            user.setMailSent(true);
        } catch (MailSendingFailedException e) {
            user.setMailSent(false);
        }
    }

    private static void sendMail(User user, String password, String subjectKey, String messageKey, String forosBaseUrl) throws MailSendingFailedException {
        MailTemplate mailTemplate = createUserMailTemplate(user, password, subjectKey, messageKey, forosBaseUrl);
        MailService mailService = ServiceLocator.getInstance().lookup(MailService.class);
        mailService.sendMail(mailTemplate);
    }

    // Create mail template to be sent to the user
    private static MailTemplate createUserMailTemplate(User user, String password, String subjectKey, String templateKey, String forosBaseUrl) {
        MailTemplate template = Templates.createMailTemplate(user, subjectKey, templateKey)
                .add("USER_FIRST_NAME", user.getFirstName())
                .add("USER_LAST_NAME", user.getLastName())
                .add("LOGIN_URL", forosBaseUrl + "/")
                .add("LOGIN", user.getEmail())
                .add("PASSWORD", password);

        if (user.getAccount() instanceof ExternalAccount) {
            ExternalAccount userAccount = (ExternalAccount) user.getAccount();
            if (userAccount.getAccountManager() != null) {
                String accountManagerData = userAccount.getAccountManager().getFirstName() + " "
                        + userAccount.getAccountManager().getLastName() + ", "
                        + userAccount.getAccountManager().getEmail();
                template.add("ACCOUNT_MANAGER", accountManagerData);
            }
        }

        return template;
    }

    public static void sendInstructionsForChangePassword(User user, String uid, String forosBaseUrl) throws MailSendingFailedException {
        MailTemplate mailTemplate = createChangePasswordsTemplate(user, uid, forosBaseUrl);
        MailService mailService = ServiceLocator.getInstance().lookup(MailService.class);
        mailService.sendMail(mailTemplate);
    }

    // Create mail template to be sent to the user
    private static MailTemplate createChangePasswordsTemplate(User user, String uid, String forosBaseUrl) {
        return Templates.createMailTemplate(user, "password.Assistance.mail.subject", "password.Assistance.mail.template")
                .add("USER_FIRST_NAME", user.getFirstName())
                .add("USER_LAST_NAME", user.getLastName())
                .add("LOGIN", user.getEmail())
                .add("CHANGE_PASSWORD_URL", changePasswordUrl(uid, forosBaseUrl));
    }

    public static String changePasswordUrl(String uid, String forosBaseUrl) {
        return String.format("%s/forgotPassword/change.action?uid=%s", forosBaseUrl, uid);
    }

    public static void sendUserUpdateMail(User user, String password, String forosBaseUrl) {
        try {
            String messageKey = (StringUtils.isNotEmpty(password)) ? "user.mail.update.templateWithoutAccountManager" : "user.mail.update.templateWithoutAccountManager.withoutPassword";
            if (user.getAccount() instanceof ExternalAccount) {
                if (((ExternalAccount) user.getAccount()).getAccountManager() != null) {
                    messageKey = (StringUtils.isNotEmpty(password)) ? "user.mail.update.template" : "user.mail.update.template.withoutPassword";
                }
            }
            sendMail(user, password, "user.mail.update.subject", messageKey, forosBaseUrl);
            user.setMailSent(true);
        } catch (MailSendingFailedException e) {
            user.setMailSent(false);
        }
    }

    public static boolean sendChannelMessageAlertMail(User loggedUser, User channelUser, String message, String channelURL, String forosBaseURL) throws MailSendingFailedException {
        MailTemplate mailTemplate = createChannelMessageAlertTemplate(loggedUser, channelUser, message, channelURL, forosBaseURL).asHtml();
        MailService mailService = ServiceLocator.getInstance().lookup(MailService.class);
        mailService.sendMail(mailTemplate);

        return true;
    }

    private static MailTemplate createChannelMessageAlertTemplate(User loggedUser, User channelUser, String message, String channelURL, String forosBaseURL) {
        return Templates.createMailTemplate(channelUser, "cmp.channel.user.message.alert.subject", "cmp.channel.user.message.alert.template")
                .add("USER_FIRST_NAME", loggedUser.getFirstName())
                .add("USER_LAST_NAME", loggedUser.getLastName())
                .add("EMAIL", loggedUser.getEmail())
                .add("CHANNEL_URL", channelURL)
                .add("MESSAGE", message)
                .add("CHANNEL_USER_EMAIL", channelUser.getEmail())
                .add("FOROS_URL", MessageFormat.format("<a href=\"{0}\">{0}</a>", forosBaseURL + "/"));
    }
}
