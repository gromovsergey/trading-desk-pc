package com.foros.security;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.security.AuthenticationToken;
import com.foros.model.security.User;
import com.foros.model.security.UserCredential;
import com.foros.session.security.LdapService;
import com.foros.session.security.UserCredentialService;
import com.foros.session.security.UserService;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.util.ValidationUtil;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.NamingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.authentication.BadCredentialsException;

@LocalBean
@Stateless
@Validations
public class UserAuthenticationValidations {

    private static final String MAC_ALGORITHM = "HmacSHA512";
    private static final String MESSAGEDIGEST_ALGORITHM = "SHA-512";
    private static final String CHARSET = "UTF-8";

    @EJB
    private UserService userService;

    @EJB
    private UserCredentialService userCredentialService;

    @EJB
    private AuthenticationService authenticationService;

    @EJB
    private ConfigService configService;

    @EJB
    private LdapService ldapService;

    @EJB
    private AccessStampService accessStampService;

    private Long expirationTime;

    @PostConstruct
    private void initialize() {
        this.expirationTime = configService.get(ConfigParameters.AUTH_TOKEN_EXPIRATION_TIME);
    }

    public void validateSwitchUser(User user) {
        ValidationContext context = ValidationUtil.createContext();

        if (user == null) {
            context.addConstraintViolation("errors.user.tokenNotAuthenticated");
            context.throwIfHasViolations();
        }

        validateUserAndThrow(context, user, false);
    }

    public void validateUsernameAndPassword(User user, String password) {
        ValidationContext context = ValidationUtil.createContext();

        if (user == null) {
            context.addConstraintViolation("errors.user.notFound");
            context.throwIfHasViolations();
        }

        if (password == null || "".equals(password)) {
            context.addConstraintViolation("errors.user.emptyPassword");
            context.throwIfHasViolations();
        }

        validateUserAndThrow(context, user, true);

        AuthenticationType authType = user.getAuthType();

        switch (authType) {
            case PSWD:
                validatePasswordUser(context, user, password);
                break;
            case LDAP:
                validateLdapUser(context, user, password);
                break;
            default:
                throw new BadCredentialsException("No authentication type");
        }
    }

    public boolean validateAuthenticationToken(AuthenticationToken authenticationToken) {
        if (authenticationToken.getLastUpdate() < System.currentTimeMillis() - expirationTime
                || !accessStampService.check(authenticationToken.getToken(), expirationTime)) {
            return false;
        }

        if (!validateUser(authenticationToken.getUser())) {
            return false;
        }

        return true;
    }

    public boolean validateUser(User user) {
        ValidationContext context = ValidationUtil.createContext();

        if (user == null) {
            return false;
        }

        validateUserImpl(context, user, false);

        return !context.hasViolations();
    }

    private void validatePasswordUser(ValidationContext context, User user, String password) {
        if (password == null || password.equals("")) {
            context.addConstraintViolation("errors.user.emptryPassword");
            context.throwIfHasViolations();
        }

        String encryptedPassword = encryptPassword(password);

        if (!user.getUserCredential().getPassword().equals(encryptedPassword)) {
            context.addConstraintViolation("errors.authentication.passwordNotMatched");
            context.throwIfHasViolations();
        }
    }

    private void validateLdapUser(ValidationContext context, User user, String password) {
        String ldapRole = user.getRole().getLdapDn();

        if (ldapRole != null) {
            try {
                if (!ldapService.userExists(user.getDn(), password, ldapRole)) {
                    context.addConstraintViolation("errors.ldapUser.authenticationFailed");
                    context.throwIfHasViolations();
                }
            } catch (NamingException e) {
                context.addConstraintViolation("errors.ldap.accessFailed");
                context.throwIfHasViolations();
            }
        }
    }

    private void validateUserAndThrow(ValidationContext context, User user, boolean checkBlocked) {
        validateUserImpl(context, user, checkBlocked);
        context.throwIfHasViolations();
    }

    private void validateUserImpl(ValidationContext context, User user, boolean checkBlocked) {
        if (checkBlocked && isUserBlocked(user)) {
            context.addConstraintViolation("errors.user.credentialBlocked");
            return;
        }

        if (user.getAccount().getRole() == AccountRole.INTERNAL &&
                !configService.get(ConfigParameters.PASSWORD_AUTHENTICATION_ALLOWED) &&
                !AuthenticationType.LDAP.equals(user.getAuthType())) {
            context.addConstraintViolation("errors.user.passwordAuthenticationNotAllowed");
            return;
        }
    }

    private boolean isUserBlocked(User user) {
        UserCredential credential = user.getUserCredential();

        Date blockedUntil = credential.getBlockedUntil();

        return blockedUntil != null && blockedUntil.after(new Timestamp(System.currentTimeMillis()));

    }

    @Validation
    public User validateTokenWithSignature(String token, byte[] signature, long timestamp, long timestampImprecision) {
        ValidationContext context = ValidationUtil.createContext();

        UserCredential credential = userCredentialService.findByToken(token);

        if (credential == null) {
            context.addConstraintViolation("errors.user.notFound");
            context.throwIfHasViolations();
        }

        if (!validateTimestamp(timestamp, timestampImprecision)) {
            context.addConstraintViolation("errors.user.tokenExpired");
            context.throwIfHasViolations();
        }

        if (credential.getRsKey() == null) {
            context.addConstraintViolation("errors.user.keyNotDefined");
            context.throwIfHasViolations();
        }

        if (!validateSignature(credential.getRsKey(), signature, timestamp)) {
            context.addConstraintViolation("errors.user.badSignature");
            context.throwIfHasViolations();
        }

        List<User> switchableUsers = userService.findSwitchableUsers(credential.getId());

        if (switchableUsers.isEmpty()) {
            context.addConstraintViolation("errors.user.notActiveUsers");
            context.throwIfHasViolations();
        }

        if (switchableUsers.size() > 1) {
            context.addConstraintViolation("errors.authentication.multiplyUsersNotAllowed");
            context.throwIfHasViolations();
        }

        User user = switchableUsers.get(0);

        validateUserAndThrow(context, user, false); // todo: does we need block this users?

        return user;
    }

    private boolean validateSignature(byte[] key, byte[] signature, long timestamp) {
        byte[] encodedHeaders = signature(String.valueOf(timestamp), key);
        return Arrays.equals(encodedHeaders, signature);
    }

    private boolean validateTimestamp(long timestamp, long timestampImprecision) {
        long currentTimestamp = System.currentTimeMillis();
        return isAllowImprecision(currentTimestamp, timestamp, timestampImprecision);
    }

    private boolean isAllowImprecision(long currentTimestamp, long passedTimestamp, long imprecision) {
        return Math.abs(currentTimestamp - passedTimestamp) < imprecision;
    }

    private byte[] signature(String text, byte[] key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, MAC_ALGORITHM);

            Mac mac = getMac();
            mac.init(keySpec);

            return mac.doFinal(text.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new BadCredentialsException("Can't believe that", e);
        } catch (GeneralSecurityException e) {
            throw new BadCredentialsException("Invalid signature", e);
        }
    }

    private MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(MESSAGEDIGEST_ALGORITHM);
    }

    public Mac getMac() throws NoSuchAlgorithmException {
        return Mac.getInstance(MAC_ALGORITHM);
    }

    private String encryptPassword(String password) {
        try {
            byte[] bytes = password.getBytes(CHARSET);
            MessageDigest md = getMessageDigest();
            md.reset();
            bytes = md.digest(bytes);
            return new String((new Base64()).encode(bytes), CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
