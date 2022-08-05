package com.foros.util.templates;

import com.foros.model.security.User;
import com.foros.util.StringUtil;

import java.util.ResourceBundle;

public final class Templates {
    private Templates() {
    }

    public static MailTemplate createMailTemplate(User user, String subject, String template) {
        ResourceBundle bundle = StringUtil.getBundle(user.getLocale());
        return new MailTemplate(user.getEmail(), bundle.getString(subject), bundle.getString(template));
    }
}
