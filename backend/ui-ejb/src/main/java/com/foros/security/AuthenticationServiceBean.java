package com.foros.security;

import com.foros.config.ConfigService;
import com.foros.model.security.AuthenticationToken;
import com.foros.model.security.ResultType;
import com.foros.model.security.User;
import com.foros.model.security.UserCredential;
import com.foros.security.generator.KeyGenerator;
import com.foros.security.generator.SimpleKeyGenerator;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserCredentialService;
import com.foros.session.security.UserService;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Stateless(name = "AuthenticationService")
public class AuthenticationServiceBean implements AuthenticationService {

    private static final Logger logger = Logger.getLogger(AuthenticationServiceBean.class.getName());

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private UserService userService;

    @EJB
    private UserCredentialService userCredentialService;

    @EJB
    private UserAuthenticationValidations userAuthenticationValidations;

    @EJB
    private AccessStampService accessStampService;

    @EJB
    private ConfigService configService;

    @EJB
    private AuditService auditService;

    private KeyGenerator keyGenerator = new SimpleKeyGenerator();

    @Override
    public AuthenticationToken findAuthenticationToken(String token) {
        AuthenticationToken authenticationToken = find(token);

        if (authenticationToken == null) {
            return null;
        }

        if (userAuthenticationValidations.validateAuthenticationToken(authenticationToken)) {
            touch(authenticationToken);
            return authenticationToken;
        } else {
            return null;
        }
    }

    private void touch(AuthenticationToken token) {
        accessStampService.touch(token.getToken());
    }

    private AuthenticationToken find(String token) {
        if (token == null) {
            return null;
        }

        return em.find(AuthenticationToken.class, token);
    }

    @Override
    public String switchUser(String token, Long userIdToSwitch, String remoteAddress) {

        AuthenticationToken authenticationToken = findAuthenticationToken(token);

        if (authenticationToken == null) {
            throw new UsernameNotFoundException("User with token " + token + " not found.");
        }

        UserCredential userCredential = authenticationToken.getUser().getUserCredential();
        List<User> switchableUsers = userService.findSwitchableUsers(userCredential.getId());

        User user = findUser(switchableUsers, userIdToSwitch);

        userAuthenticationValidations.validateSwitchUser(user);

        discardToken(authenticationToken);

        AuthenticationToken newToken = createToken(user, remoteAddress);

        return newToken.getToken();
    }

    private void discardToken(AuthenticationToken token) {
        em.remove(token);
    }

    @Override
    public String login(String username, String password, Long priorityUserId, String remoteAddress) {
        User user = findUser(username, priorityUserId);

        try {
            userAuthenticationValidations.validateUsernameAndPassword(user, password);

            loginSuccessful(user, remoteAddress);

            return createToken(user, remoteAddress).getToken();
        } catch(ConstraintViolationException e) {
            logger.info("Authentication failed, cause:");
            for (ConstraintViolation violation : e.getConstraintViolations()) {
                logger.info("\t - " + violation.getMessage());
            }

            loginFailed(username, user, remoteAddress);

            throw e;
        } catch (RuntimeException e) {
            loginFailed(username, user, remoteAddress);

            throw e;
        }
    }

    private void loginFailed(String username, User user, String remoteAddress) {
        if (user != null && user.getUserCredential() != null) {
            userCredentialService.authenticationFailed(user.getUserCredential().getId(), remoteAddress);
            audit(user, ResultType.FAILURE, remoteAddress);
            logger.info("User [" + user.getId() + "] with credentials '" + user.getUserCredential().getEmail() + "': authentication failed from address " + remoteAddress);
        } else {
            logger.info("Unknown user authentication with credentials '" + username + "' failed from address " + remoteAddress);
        }
    }

    private void loginSuccessful(User user, String remoteAddress) {
        userCredentialService.authenticationSuccess(user.getUserCredential().getId(), remoteAddress);
        audit(user, ResultType.SUCCESS, remoteAddress);

        logger.info("User [" + user.getId() + "] with credentials '" + user.getUserCredential().getEmail() + "': authentication successful from address " + remoteAddress);
    }

    private void audit(User user, ResultType resultType, String remoteAddress) {
        auditService.logLogin(user.getEmail(), resultType, remoteAddress, user.getId());
    }

    private User findUser(String username, Long priorityUserId) {
        UserCredential credential = userCredentialService.writeLockByEmail(username);

        if (credential == null) {
            return null;
        }

        List<User> switchableUsers = userService.findSwitchableUsers(credential.getId());

        if (switchableUsers.isEmpty()) {
            return null;
        }

        return findUser(switchableUsers, priorityUserId);
    }

    private User findUser(List<User> users, Long userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }

        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    private AuthenticationToken createToken(User user, String remoteAddress) {
        AuthenticationToken authenticationToken =
                new AuthenticationToken(keyGenerator.generate(128), remoteAddress, user);

        em.persist(authenticationToken);

        return authenticationToken;
    }

    @Override
    public void removeToken(String token) {
        AuthenticationToken authenticationToken = find(token);

        if (authenticationToken == null) {
            return;
        }

        discardToken(authenticationToken);
    }

}
