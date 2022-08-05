package com.foros.service.mock;

import com.foros.model.security.OwnedEntity;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.CurrentUserServiceBean;

import java.util.Arrays;
import java.util.Set;

public class CurrentUserServiceMock implements CurrentUserService {

    private CurrentUserServiceBean currentUserServiceBean;

    @Override
    public boolean is(User user) {
        return currentUserServiceBean.is(user);
    }

    @Override
    public boolean isOwnerOf(OwnedEntity entity) {
        return currentUserServiceBean.isOwnerOf(entity);
    }

    @Override
    public boolean hasAccessTo(OwnedEntity entity) {
        return currentUserServiceBean.hasAccessTo(entity);
    }

    @Override
    public boolean isManagerOf(OwnedEntity entity) {
        return currentUserServiceBean.isManagerOf(entity);
    }

    @Override
    public boolean isManagerOf(Long accountId) {
        return currentUserServiceBean.isManagerOf(accountId);
    }

    @Override
    public boolean isCurrentAccount(Long accountId) {
        return currentUserServiceBean.isCurrentAccount(accountId);
    }

    @Override
    public boolean isInternal() {
        return currentUserServiceBean.isInternal();
    }

    @Override
    public boolean isExternal() {
        return currentUserServiceBean.isExternal();
    }

    @Override
    public boolean inAccountRole(AccountRole role) {
        return currentUserServiceBean.inAccountRole(role);
    }

    @Override
    public boolean isAccountManager() {
        return currentUserServiceBean.isAccountManager();
    }

    @Override
    public boolean isAdvertiserAccountManager() {
        return currentUserServiceBean.isAdvertiserAccountManager();
    }

    @Override
    public boolean isPublisherAccountManager() {
        return currentUserServiceBean.isPublisherAccountManager();
    }

    @Override
    public boolean isISPAccountManager() {
        return currentUserServiceBean.isISPAccountManager();
    }

    @Override
    public boolean isCMPAccountManager() {
        return currentUserServiceBean.isCMPAccountManager();
    }

    @Override
    public boolean isAgencyOf(OwnedEntity entity) {
        return currentUserServiceBean.isAgencyOf(entity);
    }

    @Override
    public boolean isAdvAccessGranted(OwnedEntity entity) {
        return currentUserServiceBean.isAdvAccessGranted(entity);
    }

    @Override
    public AccountRole getAccountRole() {
        return currentUserServiceBean.getAccountRole();
    }

    @Override
    public boolean inRole(AccountRole... roles) {
        return Arrays.asList(roles).contains(getAccountRole());
    }

    @Override
    public Long getAccountId() {
        return currentUserServiceBean.getAccountId();
    }

    @Override
    public Long getUserId() {
        return currentUserServiceBean.getUserId();
    }

    @Override
    public boolean isAdvertiserLevelRestricted() {
        return currentUserServiceBean.isAdvertiserLevelRestricted();
    }

    @Override
    public boolean isPublisherOf(OwnedEntity entity) {
        return currentUserServiceBean.isPublisherOf(entity);
    }

    @Override
    public boolean isSiteAccessGranted(OwnedEntity entity) {
        return currentUserServiceBean.isSiteAccessGranted(entity);
    }

    public boolean isSiteLevelRestricted() {
        return currentUserServiceBean.isSiteLevelRestricted();
    }

    @Override
    public Long getUserCredentialId() {
        return currentUserServiceBean.getUserCredentialId();
    }

    @Override
    public boolean isInternalWithRestrictedAccess() {
        return currentUserServiceBean.isInternalWithRestrictedAccess();
    }

    @Override
    public Set<Long> getAccessAccountIds() {
        return currentUserServiceBean.getAccessAccountIds();
    }

    @Override
    public User getUser() {
        return currentUserServiceBean.getUser();
    }

    public void setCurrentUserServiceBean(CurrentUserServiceBean currentUserServiceBean) {
        this.currentUserServiceBean = currentUserServiceBean;
    }
}
