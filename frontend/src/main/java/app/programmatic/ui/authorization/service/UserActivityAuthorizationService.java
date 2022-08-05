package app.programmatic.ui.authorization.service;

public interface UserActivityAuthorizationService extends AuthorizationService {

    boolean wasActiveInLastPeriod(Long userCredentialId, long periodInMinutes);

    void notifyUserActivity(Long userId);
}
