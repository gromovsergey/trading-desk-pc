package com.foros.session;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.util.templates.MailTemplate;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless(name = "MailService")
public class MailServiceBean implements MailService {
    private static final Logger logger = Logger.getLogger(MailServiceBean.class.getName());

    @EJB
    private ConfigService configService;

    @Resource(name = "mail/ForosMail")
    javax.mail.Session mailSession;

    @PostConstruct
    public void init() {
        String user = configService.get(ConfigParameters.MAIL_USER);
        String password = configService.get(ConfigParameters.MAIL_PASSWORD);
        String host = configService.get(ConfigParameters.MAIL_HOST);
        if (user != null
                && password != null
                && host != null) {
            String protocol = configService.get(ConfigParameters.MAIL_PROTOCOL);
            Integer port = configService.get(ConfigParameters.MAIL_PORT);

            URLName urlName = new URLName(protocol, host, port, null, user, password);

            mailSession.setPasswordAuthentication(
                    urlName,
                    new PasswordAuthentication(user, password));
            logger.info("Password auth for mail server was set");
        }
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public void sendMail(MailTemplate template) throws MailSendingFailedException {
        try {
            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(configService.get(ConfigParameters.MAIL_FROM)));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(template.getEmail(), false));
            msg.setSubject(template.getSubject());
            String mailContent = template.generate();
            if (template.isHtmlContent()) {
                msg.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
                msg.setHeader("Content-Transfer-Encoding", "quoted-printable");
                msg.setContent(mailContent, "text/html; charset=\"utf-8\"");
            } else {
                msg.setText(mailContent);
            }

            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Can't send email to '" + template.getEmail() + "', subj:'" + template.getSubject() + "'", ex);
            throw new MailSendingFailedException(ex);
        }
    }
}
