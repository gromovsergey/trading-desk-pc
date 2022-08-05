package com.foros.test;

import com.foros.AbstractRestrictionsBeanTest.PermissionsSet;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.ExternalAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AccountTypeCCGType;
import com.foros.model.security.InternalAccessType;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.model.template.CreativeTemplate;
import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.restriction.registry.PermissionRegistryService;
import com.foros.security.AccountRole;
import com.foros.test.factory.AccountTestFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.CmpAccountTestFactory;
import com.foros.test.factory.ExternalAccountTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.test.factory.IspAccountTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.UserRoleTestFactory;
import com.foros.test.factory.UserTestFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class UserDefinitionFactory {
    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager entityManager;

    @Autowired
    private InternalAccountTestFactory internalAccountTF;

    @Autowired
    private IspAccountTestFactory ispAccountTF;

    @Autowired
    private CmpAccountTestFactory cmpAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private UserTestFactory userTF;

    @Autowired
    private PermissionRegistryService permissionRegistryService;

    @Autowired
    private UserRoleTestFactory userRoleTF;

    @Autowired
    private AdvertiserAccountTypeTestFactory accountTypeTF;

    public UserDefinition internalAllAccess;
    public UserDefinition internalUserAccountAccess;
    public UserDefinition internalUserAccountNoAccess;
    public UserDefinition internalMultipleAccountsAccess;
    public UserDefinition internalMultipleAccountsNoAccess;
    public UserDefinition internalNoAccess;

    public UserDefinition ispManagerAllAccess1;
    public UserDefinition ispManagerAllAccess2;
    public UserDefinition ispManagerNoAccess;

    public UserDefinition cmpManagerAllAccess1;
    public UserDefinition cmpManagerAllAccess2;
    public UserDefinition cmpManagerNoAccess;

    public UserDefinition advertiserManagerAllAccess1;
    public UserDefinition advertiserManagerAllAccess2;
    public UserDefinition advertiserManagerNoAccess;

    public UserDefinition publisherManagerAllAccess1;
    public UserDefinition publisherManagerAllAccess2;
    public UserDefinition publisherManagerNoAccess;

    public UserDefinition ispAllAccess1;
    public UserDefinition ispAllAccess2;
    public UserDefinition ispNoAccess;

    public UserDefinition publisherAllAccess1;
    public UserDefinition publisherAllAccess2;
    public UserDefinition publisherNoAccess;

    public UserDefinition advertiserAllAccess1;
    public UserDefinition advertiserAllAccess2;
    public UserDefinition advertiserNoAccess;

    public UserDefinition cmpAllAccess1;
    public UserDefinition cmpAllAccess2;
    public UserDefinition cmpNoAccess;

    public UserDefinition agencyAllAccess1;
    public UserDefinition agencyAllAccess2;
    public UserDefinition agencyNoAccess;

    private List<UserDefinition> definitions = new ArrayList<UserDefinition>();

    @PostConstruct
    public void init () throws Exception {
        createPredefined();
    }

    @PreDestroy
    public void destroy() {
        definitions.clear();
    }

    public UserDefinition createPersistent(AccountRole accountRole, PermissionsSet permissionsSet) {
        return create(true, accountRole, permissionsSet, AccountRole.INTERNAL == accountRole ? InternalAccessType.ALL_ACCOUNTS : null);
    }

    public UserDefinition createPersistent(AccountRole accountRole, PermissionsSet permissionsSet, InternalAccessType accessType) {
        return create(true, accountRole, permissionsSet, accessType);
    }

    public UserDefinition create(AccountRole accountRole) {
        return create(false, accountRole, PermissionsSet.NONE, AccountRole.INTERNAL == accountRole ? InternalAccessType.ALL_ACCOUNTS : null);
    }

    public UserDefinition create(AccountRole accountRole, PermissionsSet permissionsSet) {
        return create(false, accountRole, permissionsSet, AccountRole.INTERNAL == accountRole ? InternalAccessType.ALL_ACCOUNTS : null);
    }

    protected final UserDefinition create(boolean persistent, AccountRole accontRole, PermissionsSet permissionsSet, InternalAccessType accessType) {
        UserDefinition result = new UserDefinition(this, persistent);
        result.setAccountRole(accontRole);
        result.setAccessType(accessType);
        result.setPermissionsSet(permissionsSet);
        definitions.add(result);
        return result;
    }

    public User createOrLoadUser(UserDefinition userDefinition, User currentUser) {
        User result;
        if (currentUser != null) {
            result = currentUser;
            if (userDefinition.isPersistent() && !entityManager.contains(currentUser)) {
                result = loadUser(currentUser.getId());
            }
        } else {
            UserDefinitionCreationContext context = new UserDefinitionCreationContext(userDefinition);
            result = userDefinition.isPersistent() ? createPersistentUser(context) : createUser(context);
        }
        return result;
    }

    private void createPredefined() {
        internalAllAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL);

        internalNoAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.NONE);

        ispManagerAllAccess1 = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL).ispManager();
        ispManagerAllAccess2 = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL).ispManager();
        ispManagerNoAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.NONE).ispManager();

        cmpManagerAllAccess1 = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL).cmpManager();
        cmpManagerAllAccess2 = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL).cmpManager();
        cmpManagerNoAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.NONE).cmpManager();

        advertiserManagerAllAccess1 = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL).advertiserManager();
        advertiserManagerAllAccess2 = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL).advertiserManager();
        advertiserManagerNoAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.NONE).advertiserManager();

        publisherManagerAllAccess1 = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL).publisherManager();
        publisherManagerAllAccess2 = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL).publisherManager();
        publisherManagerNoAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.NONE).publisherManager();

        ispAllAccess1 = createPersistent(AccountRole.ISP, PermissionsSet.ALL).managedBy(ispManagerAllAccess1);
        ispAllAccess2 = createPersistent(AccountRole.ISP, PermissionsSet.ALL).managedBy(ispManagerAllAccess2);
        ispNoAccess = createPersistent(AccountRole.ISP, PermissionsSet.NONE);

        advertiserAllAccess1 = createPersistent(AccountRole.ADVERTISER, PermissionsSet.ALL).managedBy(advertiserManagerAllAccess1);
        advertiserAllAccess2 = createPersistent(AccountRole.ADVERTISER, PermissionsSet.ALL).managedBy(advertiserManagerAllAccess2);
        advertiserNoAccess = createPersistent(AccountRole.ADVERTISER, PermissionsSet.NONE);

        cmpAllAccess1 = createPersistent(AccountRole.CMP, PermissionsSet.ALL).managedBy(cmpManagerAllAccess1);
        cmpAllAccess2 = createPersistent(AccountRole.CMP, PermissionsSet.ALL).managedBy(cmpManagerAllAccess2);
        cmpNoAccess = createPersistent(AccountRole.CMP, PermissionsSet.NONE);

        publisherAllAccess1 = createPersistent(AccountRole.PUBLISHER, PermissionsSet.ALL).managedBy(publisherManagerAllAccess1);
        publisherAllAccess2 = createPersistent(AccountRole.PUBLISHER, PermissionsSet.ALL).managedBy(publisherManagerAllAccess2);
        publisherNoAccess = createPersistent(AccountRole.PUBLISHER, PermissionsSet.NONE);

        agencyAllAccess1 = createPersistent(AccountRole.AGENCY, PermissionsSet.ALL).managedBy(advertiserManagerAllAccess1);
        agencyAllAccess2 = createPersistent(AccountRole.AGENCY, PermissionsSet.ALL).managedBy(advertiserManagerAllAccess2);
        agencyNoAccess = createPersistent(AccountRole.AGENCY, PermissionsSet.NONE);


        internalUserAccountNoAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL, InternalAccessType.USER_ACCOUNT);
        internalMultipleAccountsAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL, InternalAccessType.MULTIPLE_ACCOUNTS);
        internalMultipleAccountsNoAccess = createPersistent(AccountRole.INTERNAL, PermissionsSet.ALL, InternalAccessType.MULTIPLE_ACCOUNTS);

        //UserDefinitionFactory.publisherAllAccess1
    }

    private User createPersistentUser(final UserDefinitionCreationContext context) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        User result = (User) tt.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                return createUser(context);
            }
        });

        return entityManager.merge(result);
    }

    private User createUser(final UserDefinitionCreationContext context) {
        createAccount(context);
        createUserRole(context);

        return doCreateUser(context);
    }

    private User loadUser(Long id) {
        return entityManager.getReference(User.class, id);
    }

    private User doCreateUser(UserDefinitionCreationContext context) {
        User resultUser = userTF.create(context.getAccount(), context.getUserRole());
        entityManager.persist(resultUser);
        return resultUser;
    }

    private UserRole createUserRole(UserDefinitionCreationContext context) {
        UserRole role = userRoleTF.create(context.getAccountRole());
        context.setUserRole(role);
        if (context.getAccountRole() == AccountRole.INTERNAL) {
            InternalAccessType accessType = context.getUserDefinition().getAccessType();
            role.setInternalAccessType(accessType);
            if (accessType == InternalAccessType.MULTIPLE_ACCOUNTS) {
                role.getAccessAccountIds().addAll(context.getUserDefinition().getAccessAccountIds());
            }
        }

        updateManagerFlag(context);
        updatePolicies(context);
        userRoleTF.persist(role);

        return role;
    }

    protected Account createAccount(final UserDefinitionCreationContext context) {
        Account account = null;

        if (context.getUserDefinition().getManagerBy() != null) {
            boolean creativeTemplateSet = context.getUserDefinition().getCreativeTemplate() != null;
            boolean creativeSizeSet = context.getUserDefinition().getCreativeSize() != null;
            boolean ccgTypesSet = !context.getUserDefinition().getCcgTypes().isEmpty();

            if ((creativeTemplateSet && creativeSizeSet) || ccgTypesSet) {
                account = createManagedCcgAwareAccount(context);
            } else {
                account = createManagedAccount(context);
            }
        } else {
            account = createInternalAccount(context);
        }

        context.persistAccount(account);
        context.setAccount(account);

        return account;
    }

    private Account createManagedCcgAwareAccount(UserDefinitionCreationContext context) {
        if (context.getUserDefinition().getAccountRole() != AccountRole.ADVERTISER) {
            throw new IllegalArgumentException("CCG types are allowed only for advertiser accounts");
        }

        AccountType accountType = createAccountType(context);
        Collection<AccountTypeCCGType> ccgTypes = context.getUserDefinition().getCcgTypes();

        Set<AccountTypeCCGType> resultCcgTypes = new HashSet<AccountTypeCCGType>();
        if (ccgTypes != null && !ccgTypes.isEmpty()) {
            accountType.setCPAFlag(CCGType.DISPLAY, true);
            accountType.setCPMFlag(CCGType.TEXT, true);
            for (AccountTypeCCGType ccgType : ccgTypes) {
                ccgType.setAccountType(accountType);
                resultCcgTypes.add(ccgType);
            }
        }
        accountType.setCcgTypes(resultCcgTypes);

        accountTypeTF.persist(accountType);

        User manager = context.getUserDefinition().getManagerBy().getUser();
        AdvertiserAccountTestFactory advertiserAccountTestFactory = (AdvertiserAccountTestFactory) context.getAccountTestFactory();
        AdvertiserAccount account = advertiserAccountTestFactory.create(accountType, (InternalAccount) manager.getAccount());
        account.setAccountManager(manager);

        advertiserAccountTestFactory.persist(account);
        return account;
    }

    private AccountType createAccountType(UserDefinitionCreationContext context) {
        CreativeTemplate creativeTemplate = context.getUserDefinition().getCreativeTemplate();
        CreativeSize creativeSize = context.getUserDefinition().getCreativeSize();

        if (creativeTemplate != null && creativeSize != null) {
            return accountTypeTF.create(creativeSize, creativeTemplate);
        }
        return accountTypeTF.create();
    }

    protected Account createManagedAccount(UserDefinitionCreationContext context) {
        User manager = context.getUserDefinition().getManagerBy().getUser();
        InternalAccount internalAccount = (InternalAccount) manager.getAccount();
        Account account = ((ExternalAccountTestFactory<?>) context.getAccountTestFactory()).create(internalAccount);

        ExternalAccount external = (ExternalAccount) account;
        external.setAccountManager(manager);

        return account;
    }

    protected Account createInternalAccount(UserDefinitionCreationContext context) {
        return (Account)context.getAccountTestFactory().create();
    }

    private void updatePolicies(UserDefinitionCreationContext context) {
        Map<String, Map<String, PermissionDescriptor>> available;

        PermissionsSet permissionsSet = context.getUserDefinition().getPermissionsSet();
        switch (permissionsSet) {
            case ALL:
                available = permissionRegistryService.getPermissions(context.getAccountRole());
                break;
            case NONE:
                available = Collections.emptyMap();
                break;
            default:
                throw new IllegalArgumentException(permissionsSet.toString());
        }
        UserRole userRole = context.getUserRole();
        Set<PolicyEntry> entries = userRole.getPolicyEntries();
        entries.clear();

        for (Map<String, PermissionDescriptor> mapEntry : available.values()) {
            for (PermissionDescriptor descriptor : mapEntry.values()) {
                PolicyEntry policyEntry = createPolicyEntiry(descriptor);
                policyEntry.setUserRole(userRole);
                entries.add(policyEntry);
            }
        }

        Collection<PolicyEntry> customPermissions = context.getUserDefinition().getCustomPermissons();
        for (PolicyEntry customPermission : customPermissions) {
            customPermission.setUserRole(userRole);
        }
        entries.addAll(customPermissions);

        Collection<PolicyEntry> removedPermissions = context.getUserDefinition().getRemovedPermissions();
        entries.removeAll(removedPermissions);
    }

    private PolicyEntry createPolicyEntiry(PermissionDescriptor descriptor) {
        PolicyEntry policyEntry = new PolicyEntry();
        policyEntry.setType(descriptor.getObjectType());
        policyEntry.setAction(descriptor.getActionName());
        policyEntry.setParameter(descriptor.getParameter() == null ? null : descriptor.getParameter().toString());
        return policyEntry;
    }

    private void updateManagerFlag(UserDefinitionCreationContext context) {
        UserRole role = context.getUserRole();
        UserDefinition ud = context.getUserDefinition();

        if (ud.isIspManager()) {
            role.setISPAccountManager(true);
        }
        if (ud.isCmpManager()) {
            role.setCMPAccountManager(true);
        }
        if (ud.isAdvertiserManager()) {
            role.setAdvertiserAccountManager(true);
        }
        if (ud.isPublisherManager()) {
            role.setPublisherAccountManager(true);
        }
    }

    protected class UserDefinitionCreationContext {
        public UserDefinitionCreationContext(UserDefinition userDefinitionWithInitialData) {
            this.userDefinition = userDefinitionWithInitialData;
        }

        private final UserDefinition userDefinition;

        public UserDefinition getUserDefinition() {
            return userDefinition;
        }

        private Account account;

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
            this.accountRole = account.getRole();
        }

        private AccountRole accountRole;

        public AccountRole getAccountRole() {
            return accountRole;
        }

        private UserRole userRole;

        public void setUserRole(UserRole userRole) {
            this.userRole = userRole;
        }

        public UserRole getUserRole() {
            return userRole;
        }

        protected AccountTestFactory getAccountTestFactory() {
            switch (userDefinition.getAccountRole()) {
            case ADVERTISER:
                return advertiserAccountTF;
            case AGENCY:
                return agencyAccountTF;
            case CMP:
                return cmpAccountTF;
            case ISP:
                return ispAccountTF;
            case PUBLISHER:
                return publisherAccountTF;
            case INTERNAL:
                return internalAccountTF;
            default:
                throw new IllegalArgumentException("Unknown account role");
            }
        }

        protected void persistAccount(Account account) {
            switch (userDefinition.getAccountRole()) {
            case ADVERTISER:
                advertiserAccountTF.persist((AdvertiserAccount)account);
                return;
            case AGENCY:
                agencyAccountTF.persist((AgencyAccount)account);
                return;
            case CMP:
                cmpAccountTF.persist((CmpAccount)account);
                return;
            case ISP:
                ispAccountTF.persist((IspAccount)account);
                return;
            case PUBLISHER:
                publisherAccountTF.persist((PublisherAccount)account);
                return;
            case INTERNAL:
                internalAccountTF.persist((InternalAccount)account);
                return;
            default:
                throw new IllegalArgumentException("Unknown account role");
            }
        }
    }
}
