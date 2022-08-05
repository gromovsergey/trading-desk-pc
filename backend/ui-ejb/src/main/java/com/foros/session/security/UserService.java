package com.foros.session.security;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.User;
import com.foros.model.security.UserCredential;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface UserService {
    int PASSWORD_MIN_LENGTH = 8;
    int PASSWORD_MAX_LENGTH = 50;
    int GENPASSWORD_LENGTH = 8;

    Long create(User user);

    void update(User user);

    void updateMaxCreditLimit(User user);

    void updateMyPreferences(User user);

    void delete(Long id);

    void undelete(Long id);

    void activate(Long id);

    void inactivate(Long id);

    void changePassword(Long id, String oldPassword, String newPassword, String repeatedPassword);

    void changePasswordByUid(Long id, String changePasswordUid, String password, String repeatedPassword);

    User getFirstUserByCredentialId(Long id);    

    void updateAdvertisers(Long id, Timestamp version, Collection<AdvertiserAccount> advertisers);

    User find(Long id);

    User view(Long id);

    void refresh(Long id);

    List<UserTO> findByRole(AccountRole role);

    EntityTO getUser(Long userId);

    List<EntityTO> getAccountManagers(Long accountId, AccountRole role);

    List<EntityTO> findByAccountNotDeleted(Long accountId);

    User getMyUser();

    String hashPassword(String password);

    boolean isPasswordStrong(String password);

    String generatePassword(int size);

    boolean resetPassword(User user);

    String createChangePasswordUid(String email);

    String createChangePasswordUid(Long userId);

    List<AdvertiserAccount> findUserAdvertisers(Long userId);

    List<EntityTO> findUserSites(Long userId);

    boolean isRoleChangeAllowed(Long id);

    UserCredential findByChangePasswordUid(String uid) throws UserNotFoundException;

    /**
     * Checks if the user with specified Id is active and belongs to active account.
     *
     * @param userId id of the user to check
     * @return true if user's status is active and its account is active too
     */
    boolean isUserActive(Long userId);

    String getUserFullName(Long id);

    User findByEmail(String email);
    
    boolean isCreateNewPassword(User user);

    List<User> findSwitchableUsers(Long userCredentialId);

    List<User> findSwitchableUsersForRole(Long currentUserId, Collection<AccountRole> role);

    boolean isMultiUserCredentials(Long credentialId);

    Set<AccountRole> findSwitchableRoles(Long userCredentialId);
}
