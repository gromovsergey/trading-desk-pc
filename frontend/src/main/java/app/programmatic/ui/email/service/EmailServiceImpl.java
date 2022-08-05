package app.programmatic.ui.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = Logger.getLogger(EmailServiceImpl.class.getName());

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${web.angularBaseUrl}")
    private String baseUrl;

    @Autowired
    private JavaMailSender javaMailSender;

    private ConcurrentLinkedQueue<MailInfo> pendingEmails = new ConcurrentLinkedQueue<>();


    public void send(List<String> emails, String subj, String text) {
        if (emails.isEmpty()) {
            return;
        }

        MimeMessage mail = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(emails.toArray(new String[emails.size()]));
            helper.setFrom(mailFrom);
            helper.setSubject(subj);
            helper.setText(text);
            javaMailSender.send(mail);
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Can't send mail to %s. Email lost.\nSubject: %s\n\nText: %s",
                    emails.stream().collect(Collectors.joining(", ")), subj, text), e);
            throw new RuntimeException(e);
        }
    }

    public void sendAsync(List<String> emails, String subj, String text) {
        pendingEmails.add(new MailInfo(emails, subj, text));
    }

    public void send(String email, String subj, String text) {
        send(Collections.singletonList(email), subj, text);
    }

    public void sendAsync(String email, String subj, String text) {
        sendAsync(Collections.singletonList(email), subj, text);
    }

    @Scheduled(fixedDelay = 5000)
    public void sendPendingEmails() {
        for (;;) {
            MailInfo mailInfo = pendingEmails.poll();
            if (mailInfo == null) {
                return;
            }

            logAsyncSend(mailInfo);
            send(mailInfo.getEmails(), mailInfo.getSubj(), mailInfo.getText());
        }
    }

    private void logAsyncSend(MailInfo mailInfo) {
        logger.info("Async send email to: " + mailInfo.getEmails().stream().collect(Collectors.joining(", ")));
    }

    private static class MailInfo {
        private List<String> emails;
        private String subj;
        private String text;

        public MailInfo(List<String> emails, String subj, String text) {
            this.emails = emails;
            this.subj = subj;
            this.text = text;
        }

        public List<String> getEmails() {
            return emails;
        }

        public String getSubj() {
            return subj;
        }

        public String getText() {
            return text;
        }
    }
}
