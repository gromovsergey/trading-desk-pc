package app.programmatic.ui.authentication.service;

import app.programmatic.ui.authentication.model.AuthenticationException;
import app.programmatic.ui.authentication.model.Credentials;
import app.programmatic.ui.authorization.service.UserActivityAuthorizationService;
import app.programmatic.ui.common.tool.password.PasswordHelper;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserCredential;
import app.programmatic.ui.user.service.UserRetrievalException;
import app.programmatic.ui.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.xml.bind.DatatypeConverter;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = Logger.getLogger(AuthenticationServiceImpl.class.getName());

    @Value("${backend.readOnlyAccessMode}")
    private boolean READ_ONLY_ACCESS_MODE;

    @Value("${usersession.timeoutInMinutes}")
    private long USER_SESSION_TIMEOUT_IN_MINUTES;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserActivityAuthorizationService authorizationService;


    public Credentials get(String email, String password, String ip) throws AuthenticationException {
        if (password == null || "".equals(password)) {
            throw new AuthenticationException("Password is not provided");
        }

        User user;
        try {
            user = userService.findUserByEmailUnrestricted(email);
        } catch (UserRetrievalException e) {
            logger.info(e.getMessage());
            throw new AuthenticationException(e.getMessage());
        }
        validateUser(user, email);
        validateUserPassword(user, password);

        return fetchCredentials(user, ip);
    }

    private void validateUser(User user, String identifier) throws AuthenticationException {
        if (user == null || user.getId() == null) {
            throw new AuthenticationException("User with identifier '" + identifier + "' not found");
        }

        if (user.getUserCredential().getBlockedUntil() != null &&
                user.getUserCredential().getBlockedUntil().isAfter(LocalDateTime.now())) {
            throw new AuthenticationException("User id = " + user.getId() + " is blocked");
        }
    }

    private void validateUserPassword(User user, String password) throws AuthenticationException {
        switch (user.getAuthenticationType()) {
            case PSWD:
                validatePasswordUser(user, password);
                break;
            case LDAP:
                validateLdapUser(user, password);
                break;
            default:
                throw new IllegalArgumentException("No authentication type");
        }
    }

    private void validatePasswordUser(User user, String password) {
        String encryptedPassword = PasswordHelper.encryptPassword(password);

        if (!user.getUserCredential().getPassword().equals(encryptedPassword)) {
            notifyPasswordEntry(user.getId(), false);
            throw new AuthenticationException("Password for user id = " + user.getId() + " not matched");
        }
        notifyPasswordEntry(user.getId(), true);
    }

    private void notifyPasswordEntry(Long userId, boolean success) {
        if (READ_ONLY_ACCESS_MODE) {
            return;
        }

        if (success) {
            userService.notifyValidPasswordEntry(userId);
        } else {
            userService.notifyInvalidPasswordEntry(userId);
        }
    }

    private void validateLdapUser(User user, String password) {
        boolean ldapAuthFailed = true;
        try {
            if (ldapService.userExists(user.getLdapDn(), password, user.getUserRole().getLdapDn())) {
                ldapAuthFailed = false;
            }
        } catch (NamingException e) {
        }
        if (ldapAuthFailed) {
            userService.notifyInvalidPasswordEntry(user.getId());
            throw new AuthenticationException("LDAP Authentication for user id = " + user.getId() + " failed");
        }
        userService.notifyValidPasswordEntry(user.getId());
    }

    private Credentials fetchCredentials(User user, String ip) {
        UserCredential userCredential = user.getUserCredential();
        if (userCredential == null) {
            throw new AuthenticationException("Api is not enabled for user");
        }
        if (userCredential.getRsToken() == null || userCredential.getRsKey() == null ||
                !authorizationService.wasActiveInLastPeriod(userCredential.getId(), USER_SESSION_TIMEOUT_IN_MINUTES)) {
            userCredential = userService.changeRsCredentialsUnrestricted(user.getId(), ip);
        }

        return new Credentials(
                userCredential.getRsToken(),
                DatatypeConverter.printBase64Binary(userCredential.getRsKey()),
                user.getUserRole().getAccountRole(),
                user.getId(),
                user.getAccountId());
    }

    @Override
    public void changeRsCredentials(String key, String token, String ip) throws AuthenticationException {
        if (READ_ONLY_ACCESS_MODE) {
            return;
        }

        User user = userService.findUserByRsKeyUnrestricted(token);
        validateUser(user, token);
        if (!DatatypeConverter.printBase64Binary(user.getUserCredential().getRsKey()).equals(key)) {
            throw new AuthenticationException("Token authentication for user identifier = '" + token + "' failed");
        }

        userService.changeRsCredentialsUnrestricted(user.getId(), ip);
    }
}
