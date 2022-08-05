package app.programmatic.ui.authorization.service;

import app.programmatic.ui.authorization.model.AuthUserInfo;
import app.programmatic.ui.user.dao.model.User;


public interface AuthorizationService {
    AuthUserInfo getAuthUserInfo();

    User getAuthUser();
}
