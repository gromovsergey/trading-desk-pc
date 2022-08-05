package app.programmatic.ui.user;

import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.common.tool.password.PasswordHelper;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.email.service.EmailService;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserCredential;
import app.programmatic.ui.user.dao.model.UserRole;
import app.programmatic.ui.user.view.UserView;

import java.util.List;
import java.util.stream.Collectors;


public class UserControllerHelper {
    private static final Character DEFAULT_STATUS = 'A';
    private static final MessageInterpolator messageInterpolator = MessageInterpolator.getDefaultMessageInterpolator();

    public static User createUserFromView(UserView userView, UserRole userRole, String password) {
        User user = fromView(userView, userRole);

        user.setAccountId(userView.getAccountId());
        user.setStatus(DEFAULT_STATUS);

        user.getUserCredential().setPassword(PasswordHelper.encryptPassword(password));

        return user;
    }

    public static User updateUserFromView(UserView userView, UserRole userRole) {
        User user = fromView(userView, userRole);
        user.setId(userView.getId());
        return user;
    }

    private static User fromView(UserView userView, UserRole userRole) {
        User user = new User();
        user.setUserCredential(new UserCredential());

        user.setFirstName(userView.getFirstName());
        user.setLastName(userView.getLastName());
        user.setEmail(userView.getEmail());
        user.setVersion(XmlDateTimeConverter.convertEpochToTimestamp(userView.getVersion()));
        user.getUserCredential().setEmail(userView.getEmail());
        user.getUserCredential().setVersion(XmlDateTimeConverter.convertEpochToTimestamp(userView.getVersion2()));
        user.setAdvertiserIds(userView.getAdvertiserIds());

        user.setUserRole(userRole);

        return user;
    }

    public static UserView toView(User user) {
        return toView(user, null);
    }

    public static UserView toView(User user, List<AdvertisingAccount> advertisers) {
        UserView result = new UserView();

        result.setId(user.getId());
        result.setAccountId(user.getAccountId());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setEmail(user.getEmail());
        result.setRoleName(user.getUserRole().getName());
        result.setRoleId(user.getUserRole().getId());
        result.setAdvertiserIds(user.getAdvertiserIds());
        if (!user.getAdvertiserIds().isEmpty() && advertisers != null) {
            String str = advertisers.stream()
                    .filter(p -> user.getAdvertiserIds().contains(p.getId()))
                    .map(AdvertisingAccount::getName)
                    .collect(Collectors.joining(", "));
            result.setAdvertisers(str);
        }
        result.setDisplayStatus(user.getMajorStatus());
        result.setVersion(XmlDateTimeConverter.convertToEpochTime(user.getVersion()));
        result.setVersion2(XmlDateTimeConverter.convertToEpochTime(user.getUserCredential().getVersion()));

        return result;
    }

    public static void sendPasswordInMail(EmailService emailService, UserView user, String password, String baseUrl) {
        try {
            emailService.sendAsync(user.getEmail(),
                    messageInterpolator.interpolate("user.mail.welcome.subject"),
                    messageInterpolator.interpolate("user.mail.welcome.template",
                            user.getFirstName(), user.getLastName(), user.getEmail(), password, baseUrl));
        } catch (Exception e) {
            ConstraintViolationBuilder<User> builder = new ConstraintViolationBuilder<>();
            builder.addViolationDescription("email", "email.error.recipientAddressRejected", user.getEmail());
            builder.throwExpectedException();
        }
    }
}
