package com.foros.session;

import com.foros.util.templates.MailTemplate;

import javax.ejb.Local;

@Local
public interface MailService {
    public void sendMail(MailTemplate template) throws MailSendingFailedException;
}
