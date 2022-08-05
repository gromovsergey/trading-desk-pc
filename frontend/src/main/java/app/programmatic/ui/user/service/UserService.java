package app.programmatic.ui.user.service;

import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.user.dao.model.PasswordChangeData;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserCredential;
import app.programmatic.ui.user.validation.ValidateUser;

import java.util.Collection;
import java.util.List;

public interface UserService {

    User find(Long id);

    Iterable<User> findAllUnrestricted(Collection<Long> ids);

    User findUnrestricted(Long id);

    User findUserByEmailUnrestricted(String email) throws UserRetrievalException;

    User findUserByRsKeyUnrestricted(String authToken);

    List<User> findUsersByAccountId(Long accountId, AccountRole accountRole);

    Long create(@ValidateUser("create") User user);

    void update(@ValidateUser("update") User user);

    void updateMyPassword(@ValidateUser("updateMyPassword") PasswordChangeData data);

    MajorDisplayStatus changeStatus(Long id, StatusOperation operation);

    UserCredential changeRsCredentialsUnrestricted(Long userId, String ip);

    void notifyInvalidPasswordEntry(Long userId);

    void notifyValidPasswordEntry(Long userId);
}
