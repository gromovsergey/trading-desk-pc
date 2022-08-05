package app.programmatic.ui.authorization.service;

import app.programmatic.ui.authorization.model.AuthUserInfo;


public interface AuthorizationServiceConfigurator {
    AuthUserInfo configure(String userToken, String ip, long sessionTimeoutInMinutes);
    AuthUserInfo configureAnonymous(String ip);
    void cleanUp();
}
