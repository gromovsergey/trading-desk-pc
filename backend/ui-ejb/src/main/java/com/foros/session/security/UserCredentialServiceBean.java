package com.foros.session.security;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.security.User;
import com.foros.model.security.UserCredential;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AuthenticationService;
import com.foros.util.StringUtil;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless(name = "UserCredentialService")
public class UserCredentialServiceBean implements UserCredentialService {

    private static final Logger logger = Logger.getLogger(UserCredentialServiceBean.class.getName());
    private static final int USER_TOKEN_FOR_SEARCH_MAX_BYTES = 64;
    private static final int USER_NAME_FOR_SEARCH_MAX_BYTES = 400;
    private static final int PASSWORD_FOR_SEARCH_MAX_LENGTH = 50;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private ConfigService configService;

    @EJB
    private AuthenticationService authenticationService;

    private int wrongAttemptsAllowed;
    private int blockingTimeInMin;

    @PostConstruct
    private void initialize() {
        wrongAttemptsAllowed = configService.get(ConfigParameters.WRONG_LOGIN_ATTEMPTS);
        blockingTimeInMin = configService.get(ConfigParameters.BLOCKING_TIME_IN_MIN);
    }

    @Override
    public Long create(UserCredential userCredential) {
        em.persist(userCredential);
        return userCredential.getId();
    }

    @Override
    public UserCredential findByEmail(String email) {
        if (isNameValid(email)) {
            try {
                return (UserCredential) em.createNamedQuery("UserCredential.findByEmail")
                        .setParameter("email", email)
                        .getSingleResult();
            } catch (NoResultException e) {
            }
        }
        return null;
    }

    @Override
    public UserCredential writeLockByEmail(String email) {
        if (isNameValid(email)) {
            try {
                return (UserCredential) em.createNamedQuery("UserCredential.findByEmail")
                        .setParameter("email", email)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .getSingleResult();
            } catch (NoResultException e) {
            }
        }
        return null;
    }

    @Override
    public UserCredential findByEmailAndPassword(String email, String password) {
        if (isNameValid(email) && isPasswordValid(password)) {
            try {
                return (UserCredential) em.createNamedQuery("UserCredential.findByEmailAndPassword")
                        .setParameter("email", email)
                        .setParameter("password", password)
                        .getSingleResult();
            } catch (NoResultException e) {
            }
        }
        return null;
    }

    @Override
    public UserCredential findByToken(String token) {
        if (isTokenValid(token)) {
            try {
                return (UserCredential) em.createNamedQuery("UserCredential.findByToken")
                        .setParameter("token", token)
                        .getSingleResult();
            } catch (NoResultException e) {
            }
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        em.remove(em.find(UserCredential.class, id));
    }

    @Override
    public UserCredential find(Long id) {
        return em.find(UserCredential.class, id);
    }

    @Override
    @Interceptors(RestrictionInterceptor.class)
    @Restrict(restriction = "User.changeRsCredentials", parameters = {"find('User', #userId)"})
    public void changeRsCredentials(Long userId) {
        User user = em.find(User.class, userId);
        UserCredential userCredential = user.getUserCredential();
        userCredential.setRsToken(generateRsToken());
        userCredential.setRsKey(generateRsKey());
        em.flush();
    }

    public void authenticationSuccess(Long credentialId, String remoteAddress) {
        UserCredential credential = find(credentialId);

        if (credential.getWrongAttempts() != null && credential.getWrongAttempts() > 0) {
            logger.info("User with credentials '" + credential.getEmail() +
                    "' unblocked after " + credential.getWrongAttempts() +
                    " wrong authentication attempts");
        }

        credential.setWrongAttempts(0);
        credential.setBlockedUntil(null);
        credential.setLastLoginIP(remoteAddress);
        credential.setLastLoginDate(new Date());
    }

    public void authenticationFailed(Long credentialId, String remoteAddress) {
        UserCredential credential = find(credentialId);

        credential.setWrongAttempts(increase(credential.getWrongAttempts()));

        if (credential.getWrongAttempts() >= wrongAttemptsAllowed) {
            Date blockDate = getBlockingTime();

            credential.setBlockedUntil(blockDate);

            logger.info("User with credentials '" + credential.getEmail() +
                    "' blocked until " + blockDate + " after " + credential.getWrongAttempts() +
                    " authentication attempts from address " + remoteAddress);
        }
    }

    private Date getBlockingTime() {
        return new Date(System.currentTimeMillis() + (blockingTimeInMin * 60L * 1000L));
    }

    private int increase(Integer wrongAttempts) {
        if (wrongAttempts == null) {
            return 1;
        } else {
            return wrongAttempts + 1;
        }
    }

    private byte[] generateRsKey() {
        try {
            return KeyGenerator.getInstance("HmacSHA512").generateKey().getEncoded(); // todo!!
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRsToken() {
        return UUID.randomUUID().toString();
    }

    private boolean isTokenValid(String token) {
        if (StringUtil.isPropertyEmpty(token) || StringUtil.getBytesCount(token) > USER_TOKEN_FOR_SEARCH_MAX_BYTES) {
            return false;
        }
        return true;
    }

    private boolean isNameValid(String name) {
        if (StringUtil.isPropertyEmpty(name) || StringUtil.getBytesCount(name) > USER_NAME_FOR_SEARCH_MAX_BYTES) {
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String Password) {
        if (StringUtil.isPropertyEmpty(Password) || Password.length() > PASSWORD_FOR_SEARCH_MAX_LENGTH) {
            return false;
        }
        return true;
    }
}
