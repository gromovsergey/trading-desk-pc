package com.foros.session;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.ExternalAccount;
import com.foros.model.security.InternalAccessType;
import com.foros.model.security.OwnedEntity;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.security.AccountRole;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless(name = "CurrentUserService")
public class CurrentUserServiceBean implements CurrentUserService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    public boolean isOwnerOf(OwnedEntity entity) {
        Long currentAccountId = getCurrentAccountId();
        switch (getAccountRole()) {
            case AGENCY:
                if (isAgencyOf(entity)) {
                    return isAdvAccessGranted(entity);
                }
                break;
            case PUBLISHER:
                if (isPublisherOf(entity)) {
                    return isSiteAccessGranted(entity);
                }
                break;
        }
        return currentAccountId.equals(entity.getAccount().getId());
    }

    @Override
    public boolean isManagerOf(OwnedEntity entity) {
        Long currentUserId = getCurrentUserId();
        Account account = entity.getAccount();
        if (account instanceof ExternalAccount) {
            User accountManger = ((ExternalAccount) account).getAccountManager();
            return accountManger != null && accountManger.getId().equals(currentUserId);
        }

        return false;
    }

    @Override
    public boolean isCurrentAccount(Long accountId) {
        return getCurrentAccountId().equals(accountId);
    }

    private Long getCurrentAccountId() {
        return getPrincipal().getAccountId();
    }

    @Override
    public boolean isManagerOf(Long accountId) {
        Long currentUserId = getCurrentUserId();
        ExternalAccount account = em.find(ExternalAccount.class, accountId);

        if (account != null && account.getAccountManager() != null) {
            return account.getAccountManager().getId().equals(currentUserId);
        }

        return false;
    }

    private Long getCurrentUserId() {
        return getPrincipal().getUserId();
    }

    private ApplicationPrincipal getPrincipal() {
        return SecurityContext.getPrincipal();
    }

    @Override
    public boolean isInternal() {
        return getAccountRole() == AccountRole.INTERNAL;
    }

    @Override
    public boolean isExternal() {
        return !isInternal();
    }

    @Override
    public boolean inAccountRole(AccountRole role) {
        return role == getAccountRole();
    }

    @Override
    public AccountRole getAccountRole() {
        return SecurityContext.getAccountRole();
    }

    @Override
    public boolean inRole(AccountRole... roles) {
        return Arrays.asList(roles).contains(getAccountRole());
    }

    @Override
    public Long getAccountId() {
        return getPrincipal().getAccountId();
    }

    @Override
    public Long getUserId() {
        return getPrincipal().getUserId();
    }

    @Override
    public Long getUserCredentialId() {
        return getPrincipal().getUserCredentialId();
    }

    @Override
    public boolean isAccountManager() {
        return getUserRole().isAccountManager();
    }

    @Override
    public boolean isAdvertiserAccountManager() {
        return getUserRole().isAdvertiserAccountManager();
    }

    @Override
    public boolean isPublisherAccountManager() {
        return getUserRole().isPublisherAccountManager();
    }

    @Override
    public boolean isISPAccountManager() {
        return getUserRole().isISPAccountManager();
    }

    @Override
    public boolean isCMPAccountManager() {
        return getUserRole().isCMPAccountManager();
    }

    @Override
    public boolean isAgencyOf(OwnedEntity entity) {
        Account account = entity.getAccount();
        if (account instanceof AdvertiserAccount) {
            AgencyAccount agencyAccount = ((AdvertiserAccount) account).getAgency();
            if (agencyAccount != null) {
                return agencyAccount.getId().equals(getCurrentAccountId());
            }
        }

        return false;
    }

    @Override
    public boolean isAdvAccessGranted(OwnedEntity entity) {
        Account account = entity.getAccount();
        if (account instanceof AdvertiserAccount) {
            User currentUser = getUser();
            AdvertiserAccount advertiserAccount = (AdvertiserAccount) account;
            if (currentUser.isAdvLevelAccessFlag()) {
                return currentUser.getAdvertisers().contains(advertiserAccount);
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAdvertiserLevelRestricted() {
        return getUser().isAdvLevelAccessFlag();
    }

    @Override
    public boolean isPublisherOf(OwnedEntity entity) {
        Site site = null;
        if (entity instanceof Site) {
            site = (Site) entity;
        } else if (entity instanceof Tag) {
            site = ((Tag) entity).getSite();
        }
        return site != null && site.getAccount().getId().equals(getCurrentAccountId());
    }

    @Override
    public boolean isSiteAccessGranted(OwnedEntity entity) {
        Site site = null;
        if (entity instanceof Site) {
            site = (Site) entity;
        } else if (entity instanceof Tag) {
            site = ((Tag) entity).getSite();
        }
        if (site != null) {
            User currentUser = getUser();
            if (currentUser.isSiteLevelAccessFlag()) {
                return currentUser.getSites().contains(site);
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isSiteLevelRestricted() {
        return getUser().isSiteLevelAccessFlag();
    }

    public User getUser() {
        return em.find(User.class, getCurrentUserId());
    }

    private UserRole getUserRole() {
        Long userRoleId = getPrincipal().getUserRoleId();
        return em.find(UserRole.class, userRoleId);
    }

    @Override
    public boolean is(User user) {
        return getCurrentUserId().equals(user.getId());
    }

    @Override
    public boolean hasAccessTo(OwnedEntity entity) {
        if (entity == null) {
            return true;
        }
        switch (getUserRole().getInternalAccessType()) {
        case MULTIPLE_ACCOUNTS:
        case USER_ACCOUNT:
            return getAccessAccountIds().contains(getInternalAccountId(entity));
        default:
            return true;
        }
    }

    private Long getInternalAccountId(OwnedEntity entity) {
        Account account = entity.getAccount();
        if (account instanceof ExternalAccount) {
            account = ((ExternalAccount) account).getInternalAccount();
        }
        return account.getId();
    }

    @Override
    public boolean isInternalWithRestrictedAccess() {
        InternalAccessType accessType = getUserRole().getInternalAccessType();
        return isInternal() && (accessType == InternalAccessType.MULTIPLE_ACCOUNTS || accessType == InternalAccessType.USER_ACCOUNT);
    }

    @Override
    public Set<Long> getAccessAccountIds() {
        User user = getUser();
        UserRole userRole = user.getRole();
        switch (userRole.getInternalAccessType()) {
            case USER_ACCOUNT:
                return Collections.singleton(user.getAccount().getId());
            case MULTIPLE_ACCOUNTS:
                Set<Long> accessAccountIds = userRole.getAccessAccountIds();
                HashSet<Long> res = new HashSet<Long>(accessAccountIds.size() + 1);
                res.addAll(accessAccountIds);
                res.add(user.getAccount().getId());
                return res;
            case ALL_ACCOUNTS:
                return null;
            default:
                throw new IllegalStateException("it is impossible");
        }
    }
}
