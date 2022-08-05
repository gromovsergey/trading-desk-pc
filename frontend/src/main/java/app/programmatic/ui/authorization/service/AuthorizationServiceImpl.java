package app.programmatic.ui.authorization.service;

import org.springframework.beans.factory.annotation.Value;
import app.programmatic.ui.authorization.model.AuthUserInfo;

import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;


@Service
public class AuthorizationServiceImpl implements AuthorizationServiceConfigurator, UserActivityAuthorizationService {
    private static final ThreadLocal<AuthUserInfo> currentUserInfoThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<User> currentUserThreadLocal = new ThreadLocal<>();

    @Value("${backend.readOnlyAccessMode}")
    private boolean READ_ONLY_ACCESS_MODE;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private UserService userService;

    @Override
    public AuthUserInfo configure(String userToken, String ip, long sessionTimeoutInMinutes) {
        AuthUserInfo currentUser = READ_ONLY_ACCESS_MODE ? findUserReadOnly(userToken, ip, sessionTimeoutInMinutes) :
                findUser(userToken, ip, sessionTimeoutInMinutes);
        return currentUser == null ? null : configureImpl(currentUser);
    }

    @Override
    public AuthUserInfo configureAnonymous(String ip) {
        return configureImpl(new AuthUserInfo(null, ip));
    }

    private AuthUserInfo configureImpl(AuthUserInfo currentUser) {
        currentUserInfoThreadLocal.set(currentUser);
        return currentUser;
    }

    @Override
    public AuthUserInfo getAuthUserInfo() {
        return currentUserInfoThreadLocal.get();
    }

    @Override
    public User getAuthUser() {
        User user = currentUserThreadLocal.get();
        if (user == null) {
            user = userService.findUnrestricted(getAuthUserInfo().getId());
            currentUserThreadLocal.set(user);
        }
        return user;
    }

    private AuthUserInfo findUser(String userToken, String ip, long sessionTimeoutInMinutes) {
        try {
            String t = String.valueOf(sessionTimeoutInMinutes);
            Long id = jdbcOperations.queryForObject(
                    "with t as (\n" +
                        "    update usercredentials set last_updated = now() " +
                        "    where rs_auth_token = ? " +
                        "    and last_updated > now() - interval '" + t + " minute'\n" +
                        "    RETURNING *\n" +
                        ")\n" +
                        "select user_id from users u " +
                        "inner join usercredentials uc using(user_credential_id) " +
                        "where uc.rs_auth_token = ? and not u.status = 'D'" +
                        "and uc.last_updated > now() - interval '" + t + " minute'",
                    new Object[] { userToken, userToken },
                    Long.class);
            return id == null ? null : new AuthUserInfo(id, ip);
        } catch (Exception e) {
            return null;
        }
    }

    private AuthUserInfo findUserReadOnly(String userToken, String ip, long sessionTimeoutInMinutes) {
        try {
            Long id = jdbcOperations.queryForObject(
                            "select user_id from users u " +
                            "inner join usercredentials uc using(user_credential_id) " +
                            "where uc.rs_auth_token = ? and not u.status = 'D'",
                    new Object[] { userToken },
                    Long.class);
            return id == null ? null : new AuthUserInfo(id, ip);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void cleanUp() {
        currentUserInfoThreadLocal.remove();
        currentUserThreadLocal.remove();
    }

    @Override
    public boolean wasActiveInLastPeriod(Long userCredentialId, long periodInMinutes) {
        if (READ_ONLY_ACCESS_MODE) {
            return true;
        }

        ArrayList<Long> result = new ArrayList<>(1);
        jdbcOperations.query("select user_credential_id from usercredentials " +
                        "    where user_credential_id = ? and last_updated > now() - interval '" +
                        String.valueOf(periodInMinutes) + " minute'",
                new Object[] { userCredentialId },
                (ResultSet rs, int ind) -> {
                    result.add(rs.getLong("user_credential_id"));
                    return null;
                });

        return !result.isEmpty();
    }

    @Override
    public void notifyUserActivity(Long userId) {
        if (READ_ONLY_ACCESS_MODE) {
            return;
        }

        jdbcOperations.execute("update usercredentials set last_updated = now() where user_credential_id = " +
                "(select user_credential_id from users where user_id = " +  userId + ")"
        );
    }
}
