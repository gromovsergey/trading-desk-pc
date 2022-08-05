package com.foros.session.channel.service;

import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.*;
import com.foros.model.security.NotManagedEntity;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountService;
import com.foros.session.bulk.OperationType;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.restriction.EntityRestrictions;
import org.apache.commons.lang.ArrayUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static com.foros.security.AccountRole.*;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "advertiser_advertising_channel", action = "view", accountRoles = {INTERNAL, AGENCY, ADVERTISER}),
        @Permission(objectType = "advertiser_advertising_channel", action = "edit", accountRoles = {INTERNAL, AGENCY, ADVERTISER}),
        @Permission(objectType = "advertiser_advertising_channel", action = "create", accountRoles = {INTERNAL, AGENCY, ADVERTISER}),
        @Permission(objectType = "advertiser_advertising_channel", action = "undelete", accountRoles = {INTERNAL}),
        @Permission(objectType = "advertiser_advertising_channel", action = "log_check", accountRoles = {INTERNAL}),

        @Permission(objectType = "internal_advertising_channel", action = "view", accountRoles = {INTERNAL}),
        @Permission(objectType = "internal_advertising_channel", action = "edit", accountRoles = {INTERNAL}),
        @Permission(objectType = "internal_advertising_channel", action = "create", accountRoles = {INTERNAL}),
        @Permission(objectType = "internal_advertising_channel", action = "undelete", accountRoles = {INTERNAL}),
        @Permission(objectType = "internal_advertising_channel", action = "log_check", accountRoles = {INTERNAL}),

        @Permission(objectType = "cmp_advertising_channel", action = "view", accountRoles = {INTERNAL, CMP}),
        @Permission(objectType = "cmp_advertising_channel", action = "edit", accountRoles = {INTERNAL, CMP}),
        @Permission(objectType = "cmp_advertising_channel", action = "create", accountRoles = {INTERNAL, CMP}),
        @Permission(objectType = "cmp_advertising_channel", action = "undelete", accountRoles = {INTERNAL}),
        @Permission(objectType = "cmp_advertising_channel", action = "log_check", accountRoles = {INTERNAL})
})
public class AdvertisingChannelRestrictions {

    private static final boolean IS_MANAGED = NotManagedEntity.Util.isManaged(BehavioralChannel.class, ExpressionChannel.class, AudienceChannel.class);

    @EJB
    private PermissionService permissionService;

    @EJB
    private SearchChannelService searchChannelService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;
    
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Restriction
    public boolean canCreate(Account account) {
        return isGranted(account, "create") && entityRestrictions.canCreate(account, IS_MANAGED) && checkAccount(account);
    }

    @Restriction
    public boolean canCreateAudience(Account account) {
        return currentUserService.isInternal() && canCreate(account);
    }

    @Restriction
    public boolean canCreateCopy(Channel ch) {
        if (ch instanceof BehavioralChannel) {
            if (ch.getVisibility() == ChannelVisibility.PUB) {
                AccountRole currentRole = currentUserService.getAccountRole();
                return isGranted(currentRole, "edit") && isGranted(currentRole, "create") && entityRestrictions.validateNotDeleted(ch);
            } else {
                return isGranted(ch, "edit") && isGranted(ch, "create") && entityRestrictions.canCreateCopy(ch, ch.getAccount());
            }
        }
        return false;
    }

    @Restriction
    public boolean canContactCMPChannelUser(Channel ch) {
        return (isCmpChannel(ch) && canUpdate(ch) && searchChannelService.hasLiveAdvertisers(ch.getId()));
    }

    private boolean isCmpChannel(Channel ch) {
        return ch.getAccount().getClass().equals(CmpAccount.class);
    }

    @Restriction
    public boolean canView(Channel ch) {
        if (ch.getVisibility() == ChannelVisibility.PUB || ch.getVisibility() == ChannelVisibility.CMP) {
            return canViewPublicCmp(ch);
        } else {
            return canViewPrivate(ch);
        }
    }

    private boolean canViewPublicCmp(Channel ch) {
        return canView() && entityRestrictions.canViewBasic(ch);
    }

    @Restriction
    public boolean canViewContent(Channel ch) {
        if (ch.getVisibility() == ChannelVisibility.PUB) {
            return canViewPublicCmp(ch);
        } else {
            return canViewPrivate(ch);
        }
    }

    private boolean canViewPrivate(Channel ch) {
        return isGranted(ch, "view") && entityRestrictions.canView(ch);
    }

    @Restriction
    public boolean canUpdate(Account account) {
        return isGranted(account, "edit") && entityRestrictions.canUpdate(account);
    }

    @Restriction
    public boolean canUpdate(Channel ch) {
        return isGranted(ch, "edit") && entityRestrictions.canUpdate(ch);
    }

    @Restriction
    public boolean canActivate(Channel ch) {
        return isGranted(ch, "edit") && entityRestrictions.canActivate(ch);
    }

    @Restriction
    public boolean canInactivate(Channel ch) {
        return isGranted(ch, "edit") && entityRestrictions.canInactivate(ch);
    }

    @Restriction
    public boolean canDelete(Channel ch) {
        return isGranted(ch, "edit") && entityRestrictions.canDelete(ch);
    }

    @Restriction
    public boolean canUndelete(Channel ch) {
        return isGranted(ch, "undelete") && entityRestrictions.canUndelete(ch);
    }

    @Restriction
    public boolean canView(Account account) {
        return isGranted(account, "view") && entityRestrictions.canView(account) && checkAccount(account);
    }

    @Restriction
    public boolean canView(AccountRole accountRole) {
        return isGranted(accountRole, "view") && entityRestrictions.canView(accountRole);
    }

    @Restriction
    public boolean canView() {
        return canView(INTERNAL) || canView(CMP) || canView(ADVERTISER) || canView(AGENCY);
    }

    @Restriction
    public boolean canViewStats(Channel ch){
        return !currentUserService.isExternal() || !ch.getDisplayStatus().equals(Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS);
    }

    @Restriction
    public boolean canSubmitToCmp(Channel channel) {
        ChannelVisibility[] allowed = getAdvertisingChannelVisibilities(channel);
        return canUpdate(channel)
                && ArrayUtils.contains(allowed, ChannelVisibility.CMP)
                && channel.getVisibility() == ChannelVisibility.PRI
                && channel.getStatus() == Status.ACTIVE
                && (channel.getQaStatus() == ApproveStatus.APPROVED)
                && (channel.getDisplayStatus().getMajor() == DisplayStatus.Major.LIVE || channel.getDisplayStatus().getMajor() == DisplayStatus.Major.LIVE_NEED_ATT);
    }

    @Restriction
    public boolean canMakePublic(Channel channel) {
        ChannelVisibility[] allowed = getAdvertisingChannelVisibilities(channel);
        return canUpdate(channel)
                && ArrayUtils.contains(allowed, ChannelVisibility.PUB)
                && channel.getVisibility() == ChannelVisibility.PRI;
    }

    @Restriction
    public boolean canMakePrivate(Channel channel) {
        ChannelVisibility[] allowed = getAdvertisingChannelVisibilities(channel);
        return canUpdate(channel)
                && ArrayUtils.contains(allowed, ChannelVisibility.PRI)
                && channel.getVisibility() == ChannelVisibility.PUB
                && currentUserService.isInternal()
                && !searchChannelService.hasLiveAdvertisers(channel.getId());
    }


    /**
     * Get allowed visibilities for expression and behavioral channels
     * @param channel channel
     * @return array of allowed visibilities for the channel
     */
    private ChannelVisibility[] getAdvertisingChannelVisibilities(Channel channel) {
        return ChannelUtils.getOwnChannelAllowedVisibilities(channel.getAccount().getRole());
    }

    private boolean isGranted(Channel ch, String action) {
        if (ch instanceof AudienceChannel && "edit".equals(action) && currentUserService.isExternal()) {
            return false;
        }
        return ChannelNamespace.ADVERTISING.equals(ch.getNamespace()) && isGranted(ch.getAccount(), action);
    }

    private boolean isGranted(Account account, String action) {
        return isGranted(account.getRole(), action);
    }

    private boolean isGranted(AccountRole accountRole, String action) {
        String objectType;
        switch (accountRole) {
            case AGENCY:
            case ADVERTISER:
                objectType = "advertiser_advertising_channel";
                break;
            case INTERNAL:
                objectType = "internal_advertising_channel";
                break;
            case CMP:
                objectType = "cmp_advertising_channel";
                break;
            default:
                return false;
        }
        return permissionService.isGranted(objectType, action);
    }

    private boolean checkAccount(Account account) {
        if (!checkAccountRole(account)) {
            return false;
        }
        return advertiserEntityRestrictions.canAccessDisplayAd(account) ||
               advertiserEntityRestrictions.canAccessChannelTargetedTextAd(account);
    }

    private boolean checkAccountRole(Account account) {
        if (account instanceof InternalAccount) {
            return true;
        } else if (account instanceof AdvertisingAccountBase) {
            return ((AdvertisingAccountBase) account).isStandalone();
        } else if (account instanceof CmpAccount) {
            return true;
        }
        return false;
    }

    @Restriction
    public boolean canMerge(Channel channel, OperationType operationType) {
        boolean isPermit;
        switch (operationType) {
            case CREATE:
                Account account = find(Account.class, channel.getAccount().getId());
                isPermit = channel instanceof AudienceChannel ? canCreateAudience(account) : canCreate(account);
                break;
            case UPDATE:
                Channel existing = find(Channel.class, channel.getId());
                isPermit = isGranted(existing, "edit") &&
                           (entityRestrictions.canUpdate(existing) ||
                            (isUndeleted(channel, existing) && canUndelete(existing)));
                break;
            default:
                throw new RuntimeException(operationType + " does not supported!");
        }
        return isPermit;
    }

    private <T extends Identifiable> T find(Class<T> entityClass, Long id) {
        if (id == null) {
            throw new EntityNotFoundException("Entity with id == null not found");
        }
        T t = em.find(entityClass, id);
        if (t == null) {
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        }
        return t;
    }

    private boolean isUndeleted(Channel entity, Channel existing) {
        return existing.getStatus() == Status.DELETED && entity.getStatus() != Status.DELETED;
    }

    @Restriction
    public boolean canApprove(Channel ch) {
        return false;
    }

    @Restriction
    public boolean canDecline(Channel ch) {
        return false;
    }

    @Restriction
    public boolean canExport(Account account) {
        return account instanceof AdvertisingAccountBase
                && canView(account);
    }

    @Restriction
    public boolean canUpload(Account account) {
        return canCreate(account) && canUpdate(account);
    }

    @Restriction
    public boolean canUpload(AccountRole accountRole) {
        return isGranted(accountRole, "create") && isGranted(accountRole, "edit") && entityRestrictions.canView(accountRole);
    }

    @Restriction
    public boolean canUpload() {
        return canUpload(INTERNAL) || canUpload(ADVERTISER) || canUpload(AGENCY) || canUpload(CMP);
    }

    @Restriction
    public boolean canUpdateLogCheck(AccountRole accountRole) {
        return isGranted(accountRole, "log_check") && entityRestrictions.canView(accountRole);
    }

    @Restriction
    public boolean canViewChannelCheck(Channel channel) {
        Account account = accountService.find(channel.getAccount().getId());
        return !account.getTestFlag() && account.getAccountType().isChannelCheck() && currentUserService.isInternal();
    }

    @Restriction
    public boolean canUpdateChannelCheck(Channel channel) {
        return canViewChannelCheck(channel) && channel.getNextCheckDate() != null && channel.getNextCheckDate().before(new Date()) &&
                isGranted(channel, "log_check") &&
                (channel.getDisplayStatus().getMajor().equals(DisplayStatus.Major.LIVE) || channel.getDisplayStatus().getMajor().equals(DisplayStatus.Major.LIVE_NEED_ATT)) &&
                channel.getAccount().getStatus().equals(Status.ACTIVE);
    }
}
