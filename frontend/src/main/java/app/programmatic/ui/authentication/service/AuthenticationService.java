package app.programmatic.ui.authentication.service;

import app.programmatic.ui.authentication.model.AuthenticationException;
import app.programmatic.ui.authentication.model.Credentials;

public interface AuthenticationService {

    Credentials get(String name, String password, String ip) throws AuthenticationException;

    void changeRsCredentials(String key, String token, String ip) throws AuthenticationException;
}
