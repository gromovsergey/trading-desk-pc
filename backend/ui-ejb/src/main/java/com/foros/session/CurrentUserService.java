package com.foros.session;

import com.foros.model.security.OwnedEntity;
import com.foros.model.security.User;
import com.foros.security.AccountRole;

import java.util.Set;
import javax.ejb.Local;

@Local
public interface CurrentUserService {
    boolean is(User user);

    boolean isOwnerOf(OwnedEntity entity);

    boolean hasAccessTo(OwnedEntity entity);

    boolean isManagerOf(OwnedEntity entity);

    boolean isManagerOf(Long accountId);

    boolean isCurrentAccount(Long accountId);

    boolean isInternal();

    boolean isExternal();

    boolean inAccountRole(AccountRole role);

    boolean isAccountManager();

    boolean isAdvertiserAccountManager();

    boolean isPublisherAccountManager();

    boolean isISPAccountManager();

    boolean isCMPAccountManager();

    boolean isAgencyOf(OwnedEntity entity);

    boolean isAdvAccessGranted(OwnedEntity entity);

    AccountRole getAccountRole();

    boolean inRole(AccountRole...roles);

    Long getAccountId();

    Long getUserId();

    User getUser();

    boolean isAdvertiserLevelRestricted();

    boolean isPublisherOf(OwnedEntity entity);

    boolean isSiteAccessGranted(OwnedEntity entity);

    boolean isSiteLevelRestricted();

    Long getUserCredentialId();

    boolean isInternalWithRestrictedAccess();

    Set<Long> getAccessAccountIds();
}
