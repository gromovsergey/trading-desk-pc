package app.programmatic.ui.user.service;

import static app.programmatic.ui.common.model.MajorDisplayStatus.DELETED;
import static app.programmatic.ui.common.model.MajorDisplayStatus.INACTIVE;

import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.changetrack.dao.model.TableName;
import app.programmatic.ui.changetrack.service.ChangeTrackerService;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareMethod;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareService;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.common.tool.password.PasswordHelper;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.user.dao.UserRepository;
import app.programmatic.ui.user.dao.model.PasswordChangeData;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserCredential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.crypto.KeyGenerator;

@Service
@PrePersistAwareService(storedValueGetter = "findUnrestricted")
@Validated
public class UserServiceImpl implements UserService {

    @Value("${usersession.maxPasswordWrongAttempts}")
    private int MAX_PASSWORD_WRONG_ATTEMPTS;

    @Value("${usersession.userBlockPeriodInMinutes}")
    private int USER_BLOCK_PERIOD_IN_MINUTES;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ChangeTrackerService changeTrackerService;

    @Override
    @Restrict(restriction = "user.view")
    public User find(Long id) {
        User user = findUnrestricted(id);
        return user;
    }

    @Override
    public Iterable<User> findAllUnrestricted(Collection<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public User findUnrestricted(Long id) {
        User result = id != null ? userRepository.findById(id).orElse(null) : null;
        if (result == null) {
            throw new EntityNotFoundException(id);
        }

        return result;
    }

    @Override
    public User findUserByEmailUnrestricted(String email) throws UserRetrievalException {
        List<User> users = userRepository.findByEmailIgnoreCase(email);
        if (users.isEmpty()) {
            return null;
        }
        if (users.size() > 1) {
            throw new UserRetrievalException(UserRetrievalException.Type.MULTI_LOGIN,
                    "Failed to fetch user with email: " + email);
        }

        User user = users.get(0);
        if (DELETED == user.getMajorStatus()) {
            throw new UserRetrievalException(UserRetrievalException.Type.DELETED,
                    "User with email: " + email + " is DELETED");
        }
        if (INACTIVE == user.getMajorStatus()) {
            throw new UserRetrievalException(UserRetrievalException.Type.INACTIVE,
                    "User with email: " + email + " is INACTIVE");
        }

        return user;
    }

    @Override
    public User findUserByRsKeyUnrestricted(String authToken) {
        User user = userRepository.findByUserCredentialRsToken(authToken);
        if (user == null) {
            throw new EntityNotFoundException(null);
        }

        return user;
    }

    @Override
    @Restrict(restriction = "account.viewAdvertising", parameters="accountId")
    public List<User> findUsersByAccountId(Long accountId, AccountRole accountRole) {
        return userRepository.findByAccountIdAndUserRoleIn(accountId, userRoleService.getAvailableForCreateRoles(accountRole));
    }

    @Transactional
    @PrePersistAwareMethod
    @Override
    @Restrict(restriction = "user.create", parameters = "user.accountId")
    public Long create(User user) {
        user.setUserRole(userRoleService.findForCreate(user.getUserRole().getId()));
        User result = userRepository.save(user);
        signalChanges(result.getId(), result.getUserCredential().getId());

        return result.getId();
    }

    @Transactional
    @PrePersistAwareMethod
    @Override
    @Restrict(restriction = "user.update")
    public void update(User user) {
        updateUnrestricted(user);
    }

    private User updateUnrestricted(User user) {
        User result = userRepository.save(user);
        signalChanges(result.getId(), result.getUserCredential().getId());
        return result;
    }

    @Transactional
    @Override
    public void updateMyPassword(PasswordChangeData data) {
        User user = userRepository.findById(authorizationService.getAuthUserInfo().getId()).orElse(null);
        user.getUserCredential().setPassword(PasswordHelper.encryptPassword(data.getNewPassword()));
    }

    @Transactional
    @Override
    @Restrict(restriction = "user.update", parameters = "id")
    public MajorDisplayStatus changeStatus(Long id, StatusOperation operation) {
        User user = findUnrestricted(id);

        switch (operation) {
            case ACTIVATE:
                user.setStatus('A');
                break;
            case INACTIVATE:
                user.setStatus('I');
                break;
            case DELETE:
                user.setStatus('D');
                break;
            default:
                throw new IllegalArgumentException("Unexpected StatusOperation: " + operation);
        }

        changeTrackerService.saveChange(TableName.USER, id);

        return user.getMajorStatus();
    }

    private void signalChanges(Long userId, Long userCredentialId) {
        HashMap<TableName, Collection<Long>> changes = new HashMap<>(2);

        changes.put(TableName.USER, Collections.singletonList(userId));
        changes.put(TableName.USER_CREDENTIAL, Collections.singletonList(userCredentialId));

        changeTrackerService.saveChanges(changes);
    }

    @Transactional
    @Override
    public UserCredential changeRsCredentialsUnrestricted(Long userId, String ip) {
        User user = userRepository.findById(userId).orElse(null);
        UserCredential credential = user.getUserCredential();

        credential.setRsToken(generateRsToken());
        credential.setRsKey(generateRsKey());

        credential.setLastLoginIP(ip);
        credential.setLastLoginDate(LocalDateTime.now());

        return updateUnrestricted(user).getUserCredential();
    }

    private byte[] generateRsKey() {
        try {
            return KeyGenerator.getInstance("HmacSHA512").generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRsToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional
    public void notifyInvalidPasswordEntry(Long userId) {
        User user = findUnrestricted(userId);
        int wrongAttempts = user.getUserCredential().getWrongAttempts() != null ? user.getUserCredential().getWrongAttempts() + 1 : 1;
        if (wrongAttempts >= MAX_PASSWORD_WRONG_ATTEMPTS) {
            wrongAttempts = 0;
            user.getUserCredential().setBlockedUntil(LocalDateTime.now().plusMinutes(USER_BLOCK_PERIOD_IN_MINUTES));
        }
        user.getUserCredential().setWrongAttempts(wrongAttempts);

        updateUnrestricted(user);
    }

    @Override
    @Transactional
    public void notifyValidPasswordEntry(Long userId) {
        User user = findUnrestricted(userId);
        user.getUserCredential().setWrongAttempts(0);
        updateUnrestricted(user);
    }
}
