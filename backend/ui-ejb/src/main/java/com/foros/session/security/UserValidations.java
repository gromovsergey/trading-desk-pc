package com.foros.session.security;

import static com.foros.session.security.UserService.PASSWORD_MAX_LENGTH;
import static com.foros.session.security.UserService.PASSWORD_MIN_LENGTH;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.ChangePasswordUid;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.security.User;
import com.foros.model.security.UserCredential;
import com.foros.model.security.UserRole;
import com.foros.security.AccountRole;
import com.foros.security.AuthenticationType;
import com.foros.session.account.AccountService;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.util.ConditionStringBuilder;
import com.foros.util.NameValuePair;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.constraint.validator.EmailValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

@LocalBean
@Stateless
@Validations
public class UserValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private ConfigService configService;

    @EJB
    private LdapService ldapService;

    @EJB
    private UserService userService;

    @EJB
    private AccountService accountService;

    @EJB
    private UserCredentialService userCredentialService;

    @EJB
    private UserRestrictions userRestrictions;

    @EJB
    private CurrencyService currencyService;

    @EJB
    private UserRoleService userRoleService;

    @Validation
    public void validateCreate(
            ValidationContext context,
            @ValidateBean(ValidationMode.CREATE) User user) {
        validateSave(context, user);

    }

    @Validation
    public void validateUpdate(
            ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) User user) {
        validateRoleChange(context, user);
        validateSave(context, user);

    }

    @Validation
    public void validateUpdateMyPreferences(
            ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) User user) {
        validateEmail(context, user);
    }

    @Validation
    public void validateUid(ValidationContext context, String uid) {
        if (StringUtil.isPropertyEmpty(uid)) {
             context.addConstraintViolation("password.Assistance.error.noUidDefined");
        }
    }

    @Validation
    public void validateEmailCaptcha(ValidationContext context, String captcha, String captchaExpected, String email) {
        validateCaptcha(context, captcha, captchaExpected);
        validateEmailConstraint(context, email);

        if (context.ok()) {
            validateUser(context, email);
        }
    }

    private void validateUser(ValidationContext context, String email) {
        if (!isUserExists(email)) {
            context.addConstraintViolation("password.Assistance.validate.emailNotFound")
                    .withValue(email)
                    .withPath("email");
        }
    }

    private boolean isUserExists(String email) {
        User user = userService.findByEmail(email);

        boolean userNotFound = user == null ||
                        (user.getAccount().getRole() == AccountRole.INTERNAL && !configService.get(ConfigParameters.PASSWORD_AUTHENTICATION_ALLOWED));

        return !userNotFound;

    }

    private void validateCaptcha(ValidationContext context, String captcha, String captchaExpected) {
        if (StringUtil.isPropertyEmpty(captcha) || !captcha.equalsIgnoreCase(captchaExpected)) {
            context.addConstraintViolation("kaptcha.error")
                    .withValue(captcha)
                    .withPath("captcha");
        }
    }

    private void validateEmailConstraint(ValidationContext context, String email) {
        if (StringUtil.isPropertyEmpty(email)) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("email");
        } else if (!EmailValidator.validateEmail(email)) {
            context.addConstraintViolation("errors.field.invalid")
                    .withPath("email");
        }
    }

    @Validation
    public void validateChangePassword(ValidationContext rootContext, Long userId, String oldPassword, String newPassword, String repeatedPassword) {
        boolean hasErrors = false;
        User user = userService.find(userId);
        em.refresh(user);

        ValidationContext context = rootContext.createSubContext(user);

        UserCredential existingUserCredential = userCredentialService.findByEmail(user.getEmail());

        if (user.getAuthType() == AuthenticationType.LDAP || user.getAuthType() == AuthenticationType.NONE) {
            throw new SecurityException("Password can be changed only for user with authentication type Password");
        }

        // new password validations
        if (StringUtils.isEmpty(newPassword)) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("newPassword");
            hasErrors = true;
        } else {
            // new password is provided, check its strength
            if (newPassword.length() < PASSWORD_MIN_LENGTH) {
                // short password
                context.addConstraintViolation("errors.field.minLength")
                        .withParameters(PASSWORD_MIN_LENGTH)
                        .withPath("newPassword");
                hasErrors = true;
            } else if (newPassword.length() > PASSWORD_MAX_LENGTH) {
                // long password
                context.addConstraintViolation("errors.field.maxlength")
                        .withParameters(PASSWORD_MAX_LENGTH)
                        .withPath("newPassword");
                hasErrors = true;
            } else if (!userService.isPasswordStrong(newPassword)) {
                // weak password
                context.addConstraintViolation("errors.passwordTooWeak")
                        .withPath("newPassword");
                hasErrors = true;
            }
        }

        // repeat password validations
        if (StringUtils.isEmpty(repeatedPassword)) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("repeatedPassword");
            hasErrors = true;
        } else if (!repeatedPassword.equals(newPassword)) {
                // new password and repeat password do not match
                context.addConstraintViolation("password.Assistance.error.differentPasswords")
                        .withPath("repeatedPassword");
                hasErrors = true;
        }


        // old password validations
        if (StringUtils.isEmpty(oldPassword)) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("oldPassword");
            // old password is provided, check if it matches with one in db
        } else if (!hasErrors && !existingUserCredential.getPassword().equals(userService.hashPassword(oldPassword))) {
                context.addConstraintViolation("errors.field.invalid")
                        .withPath("oldPassword");
                return;
        }
    }

    @Validation
    public void validateForgotPasswordChange(ValidationContext validationContext, Long credentialId, String changePasswordUid, String password, String repeatedPassword) {
        UserCredential usercredential = userCredentialService.find(credentialId);
        ValidationContext context = validationContext.createSubContext(usercredential);

        User updateUser = null;
        if(StringUtil.isPropertyNotEmpty(usercredential.getId().toString())) {
            updateUser = userService.getFirstUserByCredentialId(usercredential.getId());
        }
        if (updateUser == null || updateUser.getAuthType() == AuthenticationType.LDAP || updateUser.getAuthType() == AuthenticationType.NONE) {
            throw new SecurityException("Password can be changed only for user with authentication type Password");
        }

        if (StringUtil.isPropertyEmpty(changePasswordUid)) {
            throw new SecurityException("Password is unset");
        }
        
        if (!checkChangePasswordUid(credentialId, changePasswordUid)
                && updateUser == null) {
            throw new SecurityException("Change password uid does not linked to this user credential. User credential Id: " + credentialId + ", change password uid: " + changePasswordUid);
        }

        // new password validations
        if (StringUtils.isEmpty(password)) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("password");
        } else {
            // new password is provided, check its strength
            if (password.length() < PASSWORD_MIN_LENGTH) {
                // short password
                context.addConstraintViolation("errors.field.minLength")
                        .withParameters(PASSWORD_MIN_LENGTH)
                        .withPath("password");
            } else if (password.length() > PASSWORD_MAX_LENGTH) {
                // long password
                context.addConstraintViolation("errors.field.maxlength")
                        .withParameters(PASSWORD_MAX_LENGTH)
                        .withPath("password");
            } else if (!userService.isPasswordStrong(password)) {
                // weak password
                context.addConstraintViolation("errors.passwordTooWeak")
                        .withPath("password");
            }
        }

        // repeat password validations
        if (StringUtils.isEmpty(repeatedPassword)) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("repeatedPassword");
        } else if (!repeatedPassword.equals(password)) {
                // new password and repeat password do not match
                context.addConstraintViolation("password.Assistance.error.differentPasswords")
                        .withPath("repeatedPassword");
        }
    }

    private void validateRoleChange(ValidationContext context, User user) {
        User existingUser = em.find(User.class, user.getId());
        if (!user.getRole().equals(existingUser.getRole()) && !userService.isRoleChangeAllowed(user.getId())) {
            context.addConstraintViolation("errors.user.roleChangeNotAllowed").withPath("role").withValue(user.getRole());
        }
    }

    @Validation
    public void validateUpdateAdvertisers(
            ValidationContext context,
            Long userId, Collection<AdvertiserAccount> advertisers) {
        User existingUser = em.find(User.class, userId);

        Account userAccount = existingUser.getAccount();
        if (userAccount.getRole() != AccountRole.AGENCY) {
            context.addConstraintViolation("errors.user.advertisers.notAgencyAccount");
            return;
        }

        Set<AdvertiserAccount> allowedAdvertisers = ((AgencyAccount) existingUser.getAccount()).getAdvertisers();
        Collection<Long> allowedAdvertisersIds = new ArrayList<Long>(allowedAdvertisers.size());
        for (AdvertiserAccount allowedAdvertiser : allowedAdvertisers) {
            allowedAdvertisersIds.add(allowedAdvertiser.getId());
        }
        for (AdvertiserAccount advertiser : advertisers) {
            if (!allowedAdvertisersIds.contains(advertiser.getId())) {
                context.addConstraintViolation("errors.invalidUserAdvertiser");
                break;
            }
        }
    }

    private void validateSave(ValidationContext context, User user) {
        validateMandatoryLDAP(context, user);
        validateEmail(context, user);
        validateLdapUser(context, user);
        validateUserRole(context, user);
        validateMaxCreditLimit(context, user);
    }

    private void validateMandatoryLDAP(ValidationContext context, User user) {
        AccountRole accountRole = accountService.find(user.getAccount().getId()).getRole();
        if (accountRole == AccountRole.INTERNAL &&
                !configService.get(ConfigParameters.PASSWORD_AUTHENTICATION_ALLOWED) &&
                !AuthenticationType.LDAP.equals(user.getAuthType())) {

            context.addConstraintViolation("errors.user.onlyLDAPAuthAllowed")
                    .withPath("authType");
        }
    }

    private void validateMaxCreditLimit(ValidationContext context, User user) {
        if (userRestrictions.canUpdateMaxCreditLimit(user.getRole().getId())) {
            BigDecimal maxCreditLimit = user.getMaxCreditLimit();
            if (maxCreditLimit == null) {
                context.addConstraintViolation("errors.field.required")
                        .withPath("maxCreditLimit");
            }
        }
    }


    private void validateLdapUser(ValidationContext context, User user) {
        if (AuthenticationType.LDAP.equals(user.getAuthType())) {
            String dn = user.getDn();
            if (StringUtil.isPropertyEmpty(dn)) {
                context.addConstraintViolation("errors.field.required")
                        .withPath("dn");
            } else if (ldapService.getAttrsByDn(dn) == null) {
                context.addConstraintViolation("InternalUser.ldap.error")
                        .withPath("dn")
                        .withValue(dn);
            } else {
                UserRole role = userRoleService.findById(user.getRole().getId());
                List<NameValuePair<String, String>> dns = ldapService.findDnsForRole(role);
                boolean find = false;
                for (NameValuePair<String, String> dnPair : dns) {
                    if (user.getDn().equals(dnPair.getValue())) {
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    context.addConstraintViolation("InternalUser.ldap.error")
                            .withPath("dn")
                            .withValue(dn);
                }
            }
        }
    }

    private void validateUserRole(ValidationContext context, User user) {
        Account account = em.find(Account.class, user.getAccount().getId());
        UserRole role = userRoleService.findById(user.getRole().getId());
        if (account.getRole() != role.getAccountRole()) {
            context.addConstraintViolation("user.error.role")
                    .withPath("role")
                    .withValue(role);
        }
    }

    private void validateEmail(ValidationContext context, User user) {

        UserRole userRole = userRoleService.findById(user.getRole().getId());
        boolean isCreatingUser = user.getId() == null;
        User existingUser = !isCreatingUser ? em.find(User.class, user.getId()) : null;
        String email = user.getEmail();
        boolean isEmailModifiedOrCreated = existingUser == null || !existingUser.getEmail().equals(email);
        if (isEmailModifiedOrCreated) {

            boolean isEmailUnavailable = false;
            boolean isInternalUser = AccountRole.INTERNAL.equals(userRole.getAccountRole());

            if (isInternalUser || !isCreatingUser) {
                isEmailUnavailable = isEmailInUse(email);
            } else if (isCreatingUser) {
                isEmailUnavailable = isEmailInUseInAccount(email, user.getAccount().getId()) || isEmailUsedByInternal(email);
            }

            if (isEmailUnavailable) {
                context.addConstraintViolation("errors.duplicate")
                        .withParameters("{user.email}")
                        .withPath("email")
                        .withValue(email);
            }


        }
    }

    private boolean isEmailInUse(String email) {
        return isEmailInUseInAccount(email, null);
    }

    private boolean isEmailInUseInAccount(String email, Long accountId) {
        ConditionStringBuilder queryString = new ConditionStringBuilder("SELECT count(u) from User u WHERE u.email = :email ");
        queryString.append(accountId != null, " and u.account.id = :id ", "");

        Query query = em.createQuery(queryString.toString());
        query.setParameter("email", email);

        if (accountId != null) {
            query.setParameter("id", accountId);
        }
        return (Long) query.getSingleResult() > 0;
    }

    private boolean isEmailUsedByInternal(String email) {
        String queryString = "SELECT count(u) FROM User u WHERE u.email = :email and u.account.role = :role";
        Query query = em.createQuery(queryString)
                .setParameter("role", AccountRole.INTERNAL)
                .setParameter("email", email);

        return (Long) query.getSingleResult() > 0L;
    }

    private boolean checkChangePasswordUid(Long id, String changePasswordUid) {
        try {
            ChangePasswordUid uid = (ChangePasswordUid) em
                    .createNamedQuery("ChangePasswordUid.findByUserAndUid")
                    .setParameter("ucid", id)
                    .setParameter("uid", changePasswordUid)
                    .getSingleResult();
            em.remove(uid);
            em.flush();
        } catch (NoResultException e) {
            return false;
        }

        return true;
    }

}
