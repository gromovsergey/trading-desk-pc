package com.foros.session.security;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.ChangePasswordUid;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.ActionType;
import com.foros.model.security.User;
import com.foros.model.security.UserCredential;
import com.foros.model.security.UserRole;
import com.foros.model.site.Site;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.security.AuthenticationType;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.status.StatusService;
import com.foros.util.CollectionUtils;
import com.foros.util.ConditionStringBuilder;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.mapper.Converter;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.codec.binary.Base64;

@Stateless(name = "UserService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class, ValidationInterceptor.class})
public class UserServiceBean implements UserService {
    private static final String[] GENERATED_PASSWORD_CHARS = new String[]{"abcdefghijklmnopqrstuvwxyz","ABCDEFGHIJKLMNOPQRSTUVWXYZ","1234567890"};
    private static final Pattern[] passwordPattern = {
        Pattern.compile("\\p{Lu}"), // uppercase
        Pattern.compile("\\p{Ll}"), // lowercase
        Pattern.compile("\\p{Digit}"), // decimal digits
        Pattern.compile("[^\\p{Lu}\\p{Ll}\\p{Digit}]") // non-alphanumeric (all other)
    };

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private ConfigService configService;

    @EJB
    private AuditService auditService;
    
    @EJB
    private StatusService statusService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private UserCredentialService userCredentialService;

    @EJB
    private UserRestrictions userRestrictions;

    public void prePersist(User user) {
        Account account = em.find(Account.class, user.getAccount().getId());
        UserRole userRole = em.find(UserRole.class, user.getRole().getId());
        
        user.setAccount(account);
        user.setRole(userRole);

        AccountRole accountRole = user.getAccount().getRole();

        if (!accountRole.equals(AccountRole.AGENCY)) {
            long flags = user.getFlags();
            flags &= ~User.ADV_LEVEL_ACCESS_FLAG;
            user.setFlags(flags);

            user.setAdvertisers(new LinkedHashSet<AdvertiserAccount>());
        }

        if (!accountRole.equals(AccountRole.PUBLISHER)) {
            long flags = user.getFlags();
            flags &= ~User.SITE_LEVEL_ACCESS_FLAG;
            user.setFlags(flags);
            user.setSites(new LinkedHashSet<Site>());
        } else if (user.isChanged("sites")) {
            Set<Site> managedSites = new LinkedHashSet<Site>();
            for (Site site : user.getSites()) {
                Site managedSite = em.getReference(Site.class, site.getId());
                managedSites.add(managedSite);
            }
            user.setSites(managedSites);
        }
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.create", parameters = "find('Account', #user.account.id)")
    @Validate(validation="User.create", parameters = "#user")
    public Long create(User user) {
        prePersist(user);
        user.setStatus(Status.ACTIVE);
        if (user.getFlags() == 0) {
            user.registerChange("flags");
        }

        if (!AuthenticationType.NONE.equals(user.getAuthType())) {
            UserCredential userCredential = userCredentialService.findByEmail(user.getEmail());
            if (userCredential == null) {
                // new credential
                userCredential = createUserCredentialInternal(user.getEmail(), user.getNewPassword());
            }
            user.setUserCredential(userCredential);
        }
        if (user.getAccount().getRole() == AccountRole.INTERNAL && !userRestrictions.canUpdateMaxCreditLimit(user.getRole().getId())) {
            user.setMaxCreditLimit(BigDecimal.ZERO);
        }
        auditService.audit(user, ActionType.CREATE);
        em.persist(user);

        user.getAccount().getUsers().add(user);

        return user.getId();
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.updateAdvertisers", parameters = "find('User', #agencyUserId)")
    @Validate(validation = "User.updateAdvertisers", parameters = {"#agencyUserId", "#advertisers"})
    public void updateAdvertisers(Long agencyUserId, Timestamp version, Collection<AdvertiserAccount> advertisers) {
        User existingUser = find(agencyUserId);

        EntityUtils.checkEntityVersion(existingUser, version);
        PersistenceUtils.performHibernateLock(em, existingUser);

        Set<AdvertiserAccount> managedAdvertisers = new LinkedHashSet<AdvertiserAccount>();
        for (AdvertiserAccount advertiser : advertisers) {
            AdvertiserAccount managedAdvertiser = em.getReference(AdvertiserAccount.class, advertiser.getId());
            managedAdvertisers.add(managedAdvertiser);
        }
        existingUser.setAdvertisers(managedAdvertisers);

        auditService.audit(existingUser, ActionType.UPDATE);
    }

    @Override
    public boolean isPasswordStrong(String password) {
        int strength = 0;
        for (Pattern pattern : passwordPattern) {
            if (pattern.matcher(password).find()) {
                strength++;
            }
        }
        
        return strength >= 3;
    }

    @Override
    public String generatePassword(int size) {
        return generateSequence(size, GENERATED_PASSWORD_CHARS);
    }

    private String generateSequence(int size, String[] sets) {
        Random r = new Random(System.currentTimeMillis());

        List<Character> sequence = new LinkedList<Character>();

        // add one from each set
        for (String set : sets) {
            sequence.add(set.charAt(r.nextInt(set.length())));
        }

        // fill rest from the random sets
        for (int i = sets.length; i < size; i++) {
            String set = sets[r.nextInt(sets.length)];
            sequence.add(set.charAt(r.nextInt(set.length())));
        }

        Collections.shuffle(sequence);
        return CollectionUtils.join(sequence, "");
    }

    @Override
    public String hashPassword(String password) {
        byte[] pwd = password.getBytes();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            pwd = md.digest(pwd);
        } catch (NoSuchAlgorithmException ex) {
            // not expected
            throw new IllegalStateException(ex);
        }

        return new String((new Base64()).encode(pwd));
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.resetPassword", parameters = "find('User', #user.id)")
    public boolean resetPassword(User user) {
        User existingUser = em.getReference(User.class, user.getId());
        UserCredential existingUserCredential = userCredentialService.findByEmail(existingUser.getEmail());
        existingUserCredential.setPassword(user.getNewPassword());

        existingUser.setUserCredential(existingUserCredential);
        auditService.audit(existingUser, ActionType.UPDATE);

        List<User> users = getCommonEmailUsers(existingUser.getEmail(), user.getId());
        for (User relatedUser : users) {
            relatedUser.setUserCredential(existingUserCredential);
            auditService.audit(relatedUser, ActionType.UPDATE);
        }

        em.flush();
        return true;
    }

    @Override
    public String createChangePasswordUid(String email) {
        UserCredential userCredential = userCredentialService.findByEmail(email);
        return createChangePasswordUid(userCredential);
    }

    @Override
    @Restrict(restriction = "User.resetPassword", parameters = "find('User', #userId)")
    public String createChangePasswordUid(Long userId) {
        User user = find(userId);
        return createChangePasswordUid(user.getUserCredential());
    }

    private String createChangePasswordUid(UserCredential userCredential) {
        String uid = generateSequence(32, GENERATED_PASSWORD_CHARS);
        ChangePasswordUid passwordUid = new ChangePasswordUid(userCredential, uid);
        em.persist(passwordUid);
        return uid;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.updateMyPreferences", parameters = "find('User', #user.id)")
    @Validate(validation="User.updateMyPreferences", parameters = "#user")
    public void updateMyPreferences(User user) {
        String[] allowedChanges = new String[] {
                "firstName",
                "lastName",
                "jobTitle",
                "email",
                "phone",
                "language"
        };

        user.retainChanges(Arrays.asList(allowedChanges));
        update(user);
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    @Restrict(restriction = "User.update", parameters = "find('User', #user.id)")
    @Validate(validation="User.update", parameters = "#user")
    public void update(User user) {
        prePersist(user);

        if (user.getFlags() == 0) {
            user.registerChange("flags");
        }

        User existingUser = find(user.getId());

        Long userCredentialDeleteId = updateUserCredential(user, existingUser);

        if (!user.getEmail().equals(existingUser.getEmail())) {
            updateUsersEmail(user, existingUser);
        }

        if (!userRestrictions.canUpdateMaxCreditLimit(user.getRole().getId())) {
            user.setMaxCreditLimit(existingUser.getMaxCreditLimit());
        }

        if (userCredentialDeleteId != null) {
            userCredentialService.delete(userCredentialDeleteId);
        }

        user = em.merge(user);

        auditService.audit(user, ActionType.UPDATE);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.updateMaxCreditLimit", parameters = "#user.role.id")
    @Validate(validation="User.update", parameters = "#user")
    public void updateMaxCreditLimit(User user) {
        auditService.audit(find(user.getId()), ActionType.UPDATE);
        em.merge(user);
    }

    private void updateUsersEmail(User user, User existingUser) {
        List<User> users = getCommonEmailUsers(existingUser.getEmail(), user.getId());
        for (User updateUser : users) {
            updateUser.setEmail(user.getEmail());
        }
    }

    private Long updateUserCredential(User user, User existingUser) {
        Long userCredentialDeleteId = null;
        UserRole userRole = em.find(UserRole.class, user.getRole().getId());

        if (AccountRole.INTERNAL.equals(userRole.getAccountRole())) {
            // if authentication or email changed for internal user update the credentials
            UserCredential existingUserCredential = existingUser.getUserCredential();
            if (!user.getEmail().equals(existingUser.getEmail())) {
                existingUserCredential.setEmail(user.getEmail());
            }

            if (!user.getAuthType().equals(existingUser.getAuthType())) {
                if (AuthenticationType.LDAP.equals(user.getAuthType())) {
                    existingUserCredential.setPassword(null);
                } else {
                    existingUserCredential.setPassword(user.getNewPassword());
                }
            }
            user.setUserCredential(existingUserCredential);
        } else {
            // external user
            if (!user.getEmail().equals(existingUser.getEmail())) {
                // new email
                UserCredential oldUserCredential = userCredentialService.findByEmail(existingUser.getEmail());
                if (AuthenticationType.PSWD.equals(user.getAuthType())) {
                    if (oldUserCredential == null) {
                        user.setUserCredential(createUserCredentialInternal(user.getEmail(), user.getNewPassword()));
                    } else {
                        oldUserCredential.setEmail(user.getEmail());
                        user.setUserCredential(oldUserCredential);
                    }
                } else {
                    // if authentication Type = none
                    if (oldUserCredential != null) {
                        if (getOldUserEmailCount(existingUser.getEmail()) == 1 || !hasUsersWithPSWDAuthType(existingUser.getEmail(), existingUser.getId())) {
                            userCredentialDeleteId = oldUserCredential.getId();
                        } else {
                            oldUserCredential.setEmail(user.getEmail());
                        }
                        user.setUserCredential(null);
                    }
                }
            } else {
                // if email not changed
                UserCredential existingUserCredential = userCredentialService.findByEmail(user.getEmail());
                if (AuthenticationType.NONE.equals(user.getAuthType())) {
                    if (existingUserCredential != null && (getOldUserEmailCount(existingUser.getEmail()) == 1 || !hasUsersWithPSWDAuthType(existingUser.getEmail(), existingUser.getId()))) {
                        userCredentialDeleteId = existingUserCredential.getId();
                    }
                    user.setUserCredential(null);
                } else {
                    // if authentication type password
                    if (existingUserCredential == null) {
                        user.setUserCredential(createUserCredentialInternal(user.getEmail(), user.getNewPassword()));
                    } else {
                        user.setUserCredential(existingUserCredential);
                    }
                }
            }
        }
        return userCredentialDeleteId;
    }

    private UserCredential createUserCredentialInternal(String email, String password) {
        UserCredential newUserCredential = new UserCredential();
        newUserCredential.setEmail(email);
        newUserCredential.setPassword(password);
        userCredentialService.create(newUserCredential);
        return userCredentialService.findByEmail(newUserCredential.getEmail());
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.delete", parameters = "find('User', #id)")
    public void delete(Long id) {
        statusService.delete(find(id));
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.undelete", parameters = "find('User', #id)")
    public void undelete(Long id) {
        statusService.undelete(find(id));
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.changePassword", parameters = "find('User', #id)")
    @Validate(validation = "User.changePassword", parameters = {"#id", "#oldPassword", "#newPassword", "#repeatedPassword"})
    public void changePassword(Long id, String oldPassword, String newPassword, String repeatedPassword) {
        User user = find(id);
        em.refresh(user);
        UserCredential existingUserCredential = userCredentialService.findByEmail(user.getEmail());

        // Should not happen, because to change password it should be reset first.
        if (existingUserCredential.getPassword() == null) {
            throw new SecurityException("Password is unset");
        }

        existingUserCredential.setPassword(hashPassword(newPassword));
        auditService.audit(user, ActionType.UPDATE);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "User.forgotPasswordChange", parameters = {"#credentialId", "#changePasswordUid", "#password", "#repeatedPassword"})
    public void changePasswordByUid(Long credentialId, String changePasswordUid, String password, String repeatedPassword) {
        UserCredential usercredential = userCredentialService.find(credentialId);
        usercredential.setPassword(hashPassword(password));
        usercredential.setWrongAttempts(0);
        usercredential.setBlockedUntil(null);

        auditService.audit(
                getFirstUserByCredentialId(usercredential.getId()),
                ActionType.UPDATE
        );

        em.flush();
    }

    public User getFirstUserByCredentialId(Long id) {
        StringBuilder query = new StringBuilder()
                .append("select u from User u ")
                .append("where u.userCredential.id = :id")
                .append(" and u.authType = :atype")
                .append(" order by u.id desc");
        List<User> users = em.createQuery(query.toString())
                .setParameter("id", id)
                .setParameter("atype", AuthenticationType.PSWD)
                .getResultList();

        return !users.isEmpty() ? users.get(0) : null;
    }
    
    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.inactivate", parameters = "find('User', #id)")
    public void inactivate(Long id) {
        statusService.inactivate(find(id));
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "User.activate", parameters = "find('User', #id)")
    public void activate(Long id) {
        statusService.activate(find(id));
    }

    @Override
    public User find(Long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User with id=" + id + " not found");
        }

        return user;
    }

    @Override
    @Restrict(restriction = "User.view", parameters = "find('User', #id)")
    public User view(Long id) {
        return find(id);
    }

    @Override
    public void refresh(Long id) {
        em.refresh(find(id));
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "#role")
    public List<UserTO> findByRole(AccountRole role) {
        boolean restrictedAccess = currentUserService.isInternalWithRestrictedAccess();
        String ql = "select new com.foros.session.security.UserTO(" +
                    " u.id, u.email, u.status," +
                    " a.id, a.name, a.displayStatusId, a.country.countryCode, " +
                    " ur.id, ur.name " +
                ") " +
                " from User u " +
                " join u.account a " +
                " join u.role ur " +
                " where a.role = :role " +
                (restrictedAccess ? " and " + SQLUtil.formatINClause("u.account.id", currentUserService.getAccessAccountIds()) : "") + 
                (!getMyUser().isDeletedObjectsVisible() ? " and u.status <> 'D'" : "");

        Query q = em.createQuery(ql);
        q.setParameter("role", role);

        //noinspection unchecked
        return q.getResultList();
    }

    @Override
    public User findByEmail(String email) {

        List<User> users = em.createNamedQuery("User.findByEmail")
                .setParameter("authType", AuthenticationType.PSWD)
                .setParameter("email", email)
                .getResultList();

        User user = !users.isEmpty() ? users.get(0) : null;

        if (user == null) {
            // if its ldap or no login
            return null;
        }
        return user;
    }

    @Override
    public EntityTO getUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId can't be null");
        }
        Query q = em.createNamedQuery("Account.findUserTOById");
        q.setParameter("id", userId);
        return (EntityTO) q.getSingleResult();
    }

    private long getAccManFlagForRole(AccountRole role) {
        switch (role) {
          case ADVERTISER:
              return UserRole.ACCOUNT_MANAGER_ADVERTISER;
          case PUBLISHER:
              return UserRole.ACCOUNT_MANAGER_PUBLISHER;
          case ISP:
              return UserRole.ACCOUNT_MANAGER_ISP;
          case AGENCY:
              return UserRole.ACCOUNT_MANAGER_ADVERTISER;
          case CMP:
              return UserRole.ACCOUNT_MANAGER_CMP;
          default:
            throw new IllegalArgumentException("Illegal account manager for role: '" + role + "'");
        }
    }

    @Override
    @Restrict(restriction = "User.findAccountManagers")
    public List<EntityTO> getAccountManagers(Long accountId, AccountRole role) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT NEW com.foros.session.security.UserByAccountTO");
        queryBuilder.append("   (u.id,u.firstName,u.lastName,u.status)");
        queryBuilder.append("FROM User u ");

        queryBuilder.append("where bitand(u.role.flags, :accManFlag) != 0 ");

        if (accountId == null) {
            if (currentUserService.isInternalWithRestrictedAccess()) {
                queryBuilder.append(" and ").append(SQLUtil.formatINClause("u.account.id", currentUserService.getAccessAccountIds()));
            }
        } else {
            queryBuilder.append(" and u.account.id = :accountId ");
        }

        if (currentUserService.isAccountManager()) {
            queryBuilder.append(" and u.id = :userId ");
        }

        if (!getMyUser().isDeletedObjectsVisible()) {
            queryBuilder.append(" and u.status <> 'D' ");
        }
        queryBuilder.append("ORDER BY u.lastName ");

        Query q = em.createQuery(queryBuilder.toString());

        q.setParameter("accManFlag", getAccManFlagForRole(role));
        if (accountId != null) {
            q.setParameter("accountId", accountId);
        }

        if (currentUserService.isAccountManager()) {
            q.setParameter("userId", currentUserService.getUserId());
        }

        @SuppressWarnings("unchecked")
        List<EntityTO> result = q.getResultList();

        return result;
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public List<EntityTO> findByAccountNotDeleted(Long accountId) {
        Query q = em.createQuery("SELECT NEW com.foros.session.security.UserByAccountTO(u.id,u.firstName,u.lastName,u.status) " +
                "FROM User u " +
                "WHERE u.account.id = :accountId AND u.status <> 'D' ORDER BY u.lastName ");
        q.setParameter("accountId", accountId);

        @SuppressWarnings("unchecked")
        List<EntityTO> result = q.getResultList();

        return result;
    }

    @Override
    public User getMyUser() {
        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        return em.find(User.class, principal.getUserId());
    }

    @Override
    @Restrict(restriction = "User.view", parameters = "find('User', #userId)")
    public List<AdvertiserAccount> findUserAdvertisers(Long userId) {
        User user = em.find(User.class, userId);
        List<AdvertiserAccount> advertisers = new ArrayList<AdvertiserAccount>(user.getAdvertisers());
        return advertisers;
    }

    @Override
    @Restrict(restriction = "User.view", parameters = "find('User', #userId)")
    public List<EntityTO> findUserSites(Long userId) {
        User user = em.find(User.class, userId);

        List<EntityTO> entities = CollectionUtils.convert(new Converter<Site, EntityTO>() {
            @Override
            public EntityTO item(Site site) {
                return new EntityTO(site.getId(), site.getName(), site.getStatus());
            }
        }, user.getSites());

        return entities;
    }

    @Override
    public boolean isRoleChangeAllowed(Long id) {
        return isRoleChangeAllowed(em.find(User.class, id));
    }

    @Override
    @Validate(validation = "User.uid", parameters = {"#uid"})
    public UserCredential findByChangePasswordUid(String uid) throws UserNotFoundException {        
        Query query = em
                .createNamedQuery("User.findByChangePasswordUid")
                .setParameter("uid", uid);

        try {
            return (UserCredential) query.getSingleResult();
        } catch (NoResultException e) {
            throw new UserNotFoundException("uid", uid);
        }
    }

    private boolean isRoleChangeAllowed(User user) {
        UserRole role = user.getRole();

        if (!role.isAdvertiserAccountManager() &&
                !role.isPublisherAccountManager() &&
                !role.isISPAccountManager() &&
                !role.isCMPAccountManager()) {
            return true;
        }

        String sql = "select count(*) from account where account_manager_id = ?";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, user.getId());

        Number res = (Number) query.getSingleResult();

        return res.intValue() == 0L;
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean isUserActive(Long userId) {
        Long result = (Long)em.createQuery("SELECT COUNT(*) FROM User u where u.id=:userId and u.status = 'A' and u.account.status = 'A' and u.authType <> :aType")
                .setParameter("userId", userId)
                .setParameter("aType", AuthenticationType.NONE)
                .getSingleResult();
        return result != 0L;
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public String getUserFullName(Long id) {
        User usr = find(id);
        String userName = StringUtil.toString(usr.getFirstName()) + " " + StringUtil.toString(usr.getLastName());
        return userName.trim();
    }

    private Long getOldUserEmailCount(String email) {
        Long result = (Long) em.createQuery("SELECT COUNT(*) FROM User u where u.email=:email and u.role.accountRole != :role")
                .setParameter("email", email)
                .setParameter("role", AccountRole.INTERNAL)
                .getSingleResult();
        return result;
    }

   private boolean hasUsersWithPSWDAuthType(String email, Long id) {
        Long result = (Long) em.createQuery("SELECT COUNT(*) FROM User u where u.email=:email and u.role.accountRole != :role and u.authType = :authType  " +
                "and u.id != :id")
                .setParameter("email", email)
                .setParameter("role", AccountRole.INTERNAL)
                .setParameter("authType", AuthenticationType.PSWD)
                .setParameter("id", id)
                .getSingleResult();

        return result != 0L;
    }

    private List<User> getCommonEmailUsers(String email, long userId) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :email and u.id != :userId")
                .setParameter("email", email)
                .setParameter("userId", userId)
                .getResultList();

        return users;
    }

    public boolean isCreateNewPassword(User user) {
        if (AuthenticationType.PSWD.equals(user.getAuthType())) {
            User existingUser = null;
            boolean emailUpdated = false;
            if (user.getId() != null) {
                existingUser = find(user.getId());
                emailUpdated = !user.getEmail().equals(existingUser.getEmail());
            }

            if (emailUpdated) {
                UserCredential existingCredential = userCredentialService.findByEmail(user.getEmail());
                UserCredential oldUserCredential = userCredentialService.findByEmail(existingUser.getEmail());
                if (existingCredential == null && oldUserCredential == null) {
                    return true;
                }
            } else {
                UserCredential uc = userCredentialService.findByEmail(user.getEmail());
                if (uc == null || uc.getPassword() == null) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public List<User> findSwitchableUsers(Long userCredentialId) {
        Set<AccountRole> permittedRoles = configService.get(ConfigParameters.PERMITTED_ACCOUNT_ROLES);

        StringBuilder sb = new StringBuilder()
                .append("from User u where ")
                .append("u.userCredential.id = :ucid ")
                .append("and u.authType <> :authtype ")
                .append("and u.status = 'A' ");

        if (!permittedRoles.isEmpty()) {
            sb.append("and u.account.role in (:roles)");
        }

        sb.append("and u.account.status = 'A'")
                .append("order by u.id desc");

        Query query = em.createQuery(sb.toString())
                .setParameter("ucid", userCredentialId)
                .setParameter("authtype", AuthenticationType.NONE);

        if (!permittedRoles.isEmpty()) {
            query.setParameter("roles", permittedRoles);
        }

        return query.getResultList();
    }

    @Override
    public List<User> findSwitchableUsersForRole(Long currentUserId, Collection<AccountRole> roles) {
        User user = find(currentUserId);
        ConditionStringBuilder queryString = new ConditionStringBuilder("from User usr where usr.userCredential = :userCred ");
        queryString.append(" and usr.authType <> :aType and usr.status = 'A' ");
        queryString.append(" and usr.account.status = 'A' ");
        queryString.append(" and usr.id <> :id ");
        queryString.append(roles != null, "and usr.account.role in (:roles)", "");

        Query query = em.createQuery(queryString.toString());
        query.setParameter("userCred", user.getUserCredential());
        query.setParameter("aType", AuthenticationType.NONE);
        query.setParameter("id", currentUserId);

        if (roles != null) {
            query.setParameter("roles", roles);
        }
        return query.getResultList();
    }

    @Override
    public boolean isMultiUserCredentials(Long credentialId) {
        return findSwitchableUsers(credentialId).size() > 1;
    }

    @Override
    public Set<AccountRole> findSwitchableRoles(Long currentUserId) {
        List<User> users = findSwitchableUsersForRole(currentUserId, Arrays.asList(AccountRole.values()));
        Set<AccountRole> res = new HashSet<>();
        for (User user : users) {
            res.add(user.getAccount().getAccountType().getAccountRole());
        }
        return res;
    }

}
