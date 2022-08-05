package com.foros.util.templates;

import com.foros.util.templates.bundle.Bundle;

import java.util.Map;

public class MailTemplate extends Template {

    private String email;
    private String subject;
    private boolean isHtmlContent = false;

    public MailTemplate(String email, String subject, String template) {
        super(template);
        this.subject = subject;
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public MailTemplate add(String key, String value) {
        return (MailTemplate) super.add(key, value);
    }

    @Override
    public MailTemplate addAll(Map<String, String> values) {
        return (MailTemplate) super.addAll(values);
    }

    @Override
    public MailTemplate addBundle(Bundle bundle) {
        return (MailTemplate) super.addBundle(bundle);
    }

    public boolean isHtmlContent() {
        return isHtmlContent;
    }

    public void setHtmlContent(boolean htmlContent) {
        isHtmlContent = htmlContent;
    }

    public MailTemplate asHtml() {
        this.isHtmlContent = true;
        return this;
    }
}
