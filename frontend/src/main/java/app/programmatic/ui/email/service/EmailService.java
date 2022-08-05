package app.programmatic.ui.email.service;

import java.util.List;

public interface EmailService {

    void send(String email, String subj, String text);

    void sendAsync(String email, String subj, String text);

    void send(List<String> emails, String subj, String text);

    void sendAsync(List<String> emails, String subj, String text);
}
