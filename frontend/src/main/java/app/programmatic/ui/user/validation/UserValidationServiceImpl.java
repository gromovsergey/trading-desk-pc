package app.programmatic.ui.user.validation;

import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.tool.password.PasswordHelper;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.strategy.CreateValidationStrategy;
import app.programmatic.ui.common.validation.strategy.NullForbiddenValidationStrategy;
import app.programmatic.ui.common.validation.strategy.UpdateValidationStrategy;
import app.programmatic.ui.common.validation.strategy.ValidationStrategy;
import app.programmatic.ui.email.service.EmailService;
import app.programmatic.ui.user.dao.UserRepository;
import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.user.dao.model.PasswordChangeData;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserOpts;
import app.programmatic.ui.user.service.UserRetrievalException;
import app.programmatic.ui.user.service.UserRoleService;
import app.programmatic.ui.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import static app.programmatic.ui.common.permission.dao.model.PermissionAction.EDIT;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.ADVERTISING_ACCOUNT;
import static app.programmatic.ui.user.service.UserRetrievalException.Type.DELETED;
import static app.programmatic.ui.user.service.UserRetrievalException.Type.INACTIVE;
import static app.programmatic.ui.user.service.UserRetrievalException.Type.MULTI_LOGIN;


public class UserValidationServiceImpl implements ConstraintValidator<ValidateUser, Object> {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ValidationStrategy createValidationStrategy = new CreateValidationStrategy(new NullForbiddenValidationStrategy());
    private static final ValidationStrategy updateValidationStrategy = new UpdateValidationStrategy(new NullForbiddenValidationStrategy());
    private static final Pattern[] passwordPattern = {
            Pattern.compile("\\p{Lu}"), // uppercase
            Pattern.compile("\\p{Ll}"), // lowercase
            Pattern.compile("\\p{Digit}"), // decimal digits
            Pattern.compile("[^\\p{Lu}\\p{Ll}\\p{Digit}]") // non-alphanumeric (all other)
    };

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @Value("${web.angularBaseUrl}")
    private String baseUrl;

    private String validateMethod;

    @Override
    public final void initialize(ValidateUser constraintAnnotation) {
        validateMethod = constraintAnnotation.value();
    }

    @Override
    public final boolean isValid(Object value, ConstraintValidatorContext context) {
        switch (validateMethod) {
            case "create": return validateCreate((User)value, context);
            case "update": return validateUpdate((User)value, context);
            case "updateMyPassword": return validateUpdateMyPassword((PasswordChangeData)value, context);
        }
        throw new IllegalArgumentException("Unknown validation method " + validateMethod);
    }

    private boolean validateCreate(User user, ConstraintValidatorContext context) {
        return validate(user, createValidationStrategy, context);
    }

    private boolean validateUpdate(User user, ConstraintValidatorContext context) {
        return validate(user, updateValidationStrategy, context);
    }

    private boolean validateUpdateMyPassword(PasswordChangeData data, ConstraintValidatorContext context) {
        ConstraintViolationBuilder<User> builder = new ConstraintViolationBuilder<>();

        User user = authorizationService.getAuthUser();

        String encryptedOldPassword = PasswordHelper.encryptPassword(data.getOldPassword());
        if (!user.getUserCredential().getPassword().equals(encryptedOldPassword)) {
            builder.addViolationDescription("oldPassword", "errors.passwordInvalid");
        }

        if (isPasswordStrong(data.getNewPassword())) {
            if (!data.getNewPassword().equals(data.getConfirmNewPassword())) {
                builder.addViolationDescription("confirmNewPassword", "errors.passwordInvalidConfirmation");
            }
        } else {
            builder.addViolationDescription("newPassword", "errors.passwordTooWeak");
        }

        return builder.buildAndPushToContext(context).isValid();
    }

    private boolean validate(User user, ValidationStrategy validationStrategy, ConstraintValidatorContext context) {
        ConstraintViolationBuilder<User> builder = new ConstraintViolationBuilder<>();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        builder.addConstraintViolation(violations);

        User existing = validationStrategy.checkBean(user.getId(), builder, id -> userRepository.findById(id).orElse(null));

        validateRole(user, builder);
        validateCredential(user, builder, validationStrategy);
        validateAccount(user, builder);
        validateEmailUnique(user, builder, validationStrategy);
        validateUpdateAdvertisers(user, existing != null ? existing.getAccountId() : user.getAccountId(), builder, validationStrategy);
        validateStatus(user, existing, builder);
        validateEmail(user, existing, builder);

        return builder.buildAndPushToContext(context).isValid();
    }

    private void validateRole(User user, ConstraintViolationBuilder<User> builder) {
        if (updateValidationStrategy.checkNotNull(user.getUserRole(), "userRole", builder)) {
            if (!userRoleService.existsForCreate(user.getUserRole().getId())) {
                builder.addViolationDescription("userRole.id", "entity.error.notFound", user.getUserRole().getId());
            }
        }
    }

    private void validateCredential(User user, ConstraintViolationBuilder<User> builder, ValidationStrategy validationStrategy) {
        if (validationStrategy.checkNotNull(user.getUserCredential(), "userCredential", builder)) {
            validationStrategy.checkBean(user.getUserCredential().getId(),
                    builder.buildSubNode("userCredential"),
                    id -> userRepository.findById(user.getId()).orElse(null).getUserCredential());
        }
    }

    private void validateAccount(User user, ConstraintViolationBuilder<User> builder) {
        AdvertisingAccount account = updateValidationStrategy.checkBean(user.getAccountId(), "accountId",
                builder.buildSubNode(""), id -> accountService.findAdvertisingUnchecked(user.getAccountId()));
        if (account == null) {
            return;
        }

        if (account.getRole() != AccountRole.AGENCY &&
                account.getRole() != AccountRole.ADVERTISER ||
                account.getAgencyId() != null) {
            builder.addViolationDescription("accountId", "entity.error.notFound", account.getId());
        }
    }

    private void validateStatus(User user, User existing, ConstraintViolationBuilder<User> builder) {
        if (existing != null && !existing.getStatus().equals(user.getStatus())) {
            builder.addViolationDescription("status", "entity.field.error.changeForbidden");
        }
    }

    private void validateEmail(User user, User existing, ConstraintViolationBuilder<User> builder) {
        if (existing != null && !existing.getEmail().equalsIgnoreCase(user.getEmail())) {
            MessageInterpolator messageInterpolator = MessageInterpolator.getDefaultMessageInterpolator();
            try {
                emailService.sendAsync(user.getEmail(),
                        MessageInterpolator.getDefaultMessageInterpolator().interpolate("user.mail.welcome.subject"),
                        messageInterpolator.interpolate("user.mail.updateEmail.template",
                                user.getFirstName() + " " + user.getLastName(), existing.getEmail(), user.getEmail(), baseUrl));
            } catch (Exception e) {
                builder.addViolationDescription("email", "email.error.recipientAddressRejected", user.getEmail());
            }
        }
    }

    private void validateEmailUnique(User user, ConstraintViolationBuilder<User> builder, ValidationStrategy validationStrategy) {
        User existing = null;
        boolean loginExists = false;
        try {
            existing = userService.findUserByEmailUnrestricted(user.getEmail());
        } catch (UserRetrievalException e) {
            if (e.getType() == MULTI_LOGIN || e.getType() == DELETED || e.getType() == INACTIVE) {
                loginExists = true;
            } else {
                throw new RuntimeException(e);
            }
        }

        if (loginExists ||
                validationStrategy == createValidationStrategy && existing != null ||
                validationStrategy == updateValidationStrategy && existing != null && !existing.getId().equals(user.getId())) {
            builder.addViolationDescription("email", "email.error.alreadyExists");
        }
    }

    private boolean isPasswordStrong(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        int strength = 0;
        for (Pattern pattern : passwordPattern) {
            if (pattern.matcher(password).find()) {
                strength++;
            }
        }

        return strength >= 3;
    }

    private void validateUpdateAdvertisers(User user, Long accountId, ConstraintViolationBuilder<User> builder, ValidationStrategy validationStrategy) {
        List<AdvertisingAccount> allowedAdvertisers = accountService.findAdvertisersByAgency(accountId);

        List<Long> allowedAdvertisersIds = allowedAdvertisers.stream()
                .map(AdvertisingAccount::getId)
                .collect(Collectors.toList());

        for (Long advertiserId : user.getAdvertiserIds()) {
            if (!allowedAdvertisersIds.contains(advertiserId)) {
                builder.addViolationDescription("advertiserIds", "user.advertisers.error.invalidAdvertiser");
                break;
            }
        }

        if (!user.getFlagsSet().contains(UserOpts.ADVERTISER_LEVEL_ACCESS) && !user.getAdvertiserIds().isEmpty()) {
            builder.addViolationDescription("advertiserIds", "user.advertisers.error.mustBeEmpty");
        }

        if (user.getFlagsSet().contains(UserOpts.ADVERTISER_LEVEL_ACCESS) && user.getAdvertiserIds().isEmpty()) {
            builder.addViolationDescription("advertiserIds", "user.advertisers.error.mustBeNotEmpty");
        }

        if (userRoleService.hasPermission(user.getUserRole().getId(), ADVERTISING_ACCOUNT, EDIT) && !user.getAdvertiserIds().isEmpty()) {
            builder.addViolationDescription("advertiserIds", "user.advertisers.error.mustBeEmpty");
        }
    }
}
