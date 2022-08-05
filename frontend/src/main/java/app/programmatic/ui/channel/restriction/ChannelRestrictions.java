package app.programmatic.ui.channel.restriction;

import com.foros.rs.client.model.advertising.channel.Channel;
import com.foros.rs.client.model.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.account.dao.model.Account;
import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.channel.dao.model.ChannelVisibility;
import app.programmatic.ui.common.permission.dao.model.PermissionAction;
import app.programmatic.ui.common.permission.dao.model.PermissionType;
import app.programmatic.ui.common.permission.service.PermissionService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;
import app.programmatic.ui.common.restriction.service.EntityRestrictions;
import app.programmatic.ui.user.dao.model.UserRole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static app.programmatic.ui.account.dao.model.AccountRole.ADVERTISER;
import static app.programmatic.ui.account.dao.model.AccountRole.AGENCY;
import static app.programmatic.ui.account.dao.model.AccountRole.INTERNAL;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.CREATE;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.EDIT;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.VIEW;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.*;

@Service
@Restrictions
public class ChannelRestrictions {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private EntityRestrictions entityRestrictions;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorizationService authorizationService;

    @Restriction("channel.search")
    public boolean canSearch() {
        return canSearch(INTERNAL, AGENCY);
    }

    @Restriction("channel.searchInternal")
    public boolean canSearchInternal() {
        return canSearch(INTERNAL);
    }

    private boolean canSearch(AccountRole... permittedRoles) {
        HashSet<AccountRole> permittedRolesSet = new HashSet(Arrays.asList(permittedRoles));
        Optional<AccountRole> correctAccountRoleOpt = Optional.ofNullable(authorizationService.getAuthUser())
                                                                        .map(u -> u.getUserRole())
                                                                        .map(ur -> ur.getAccountRole())
                                                                        .filter(ar -> permittedRolesSet.contains(ar));
        if (correctAccountRoleOpt.isEmpty()) {
            return false;
        }

        return permissionService.isGranted(INTERNAL_ADVERTISING_CHANNEL, VIEW) ||
                permissionService.isGranted(ADVERTISER_ADVERTISING_CHANNEL, VIEW);
    }

    @Restriction("channel.createInternal")
    public boolean canCreateInternal() {
        return permissionService.isGranted(INTERNAL_ADVERTISING_CHANNEL, CREATE);
    }

    @Restriction("channel.view")
    public boolean canView(AccountRole accountRole) {
        return checkChannelPermissions(accountRole, VIEW) && entityRestrictions.canView(accountRole);
    }

    @Restriction("channel.viewContent")
    public boolean canViewContent(Channel channel) {
        Account account = accountService.findAccountUnchecked(channel.getAccount().getId());
        if (authorizationService.getAuthUser().getUserRole().getAccountRole() != INTERNAL ||
                ChannelVisibility.PRI.toString().equals(channel.getVisibility())) {

            return checkChannelPermissions(account.getRole(), VIEW) &&
                    entityRestrictions.canViewEdit(account) &&
                    channel.getStatus() != Status.DELETED;
        }

        return (canView(INTERNAL) || canView(ADVERTISER) || canView(AGENCY)) &&
                    channel.getStatus() != Status.DELETED;
    }

    @Restriction("channel.updateChannels")
    public boolean canUpdateChannels(Long accountId) {
        Account account = accountService.findAccountUnchecked(accountId);
        if (!checkChannelPermissions(account.getRole(), EDIT)) {
            return false;
        }
        return entityRestrictions.canViewEdit(account);
    }

    private boolean checkChannelPermissions(AccountRole accountRole, PermissionAction action) {
        PermissionType permissionType;
        switch (accountRole) {
            case AGENCY:
            case ADVERTISER:
                permissionType = ADVERTISER_ADVERTISING_CHANNEL;
                break;
            case INTERNAL:
                permissionType = INTERNAL_ADVERTISING_CHANNEL;
                break;
            default:
                return false;
        }
        return permissionService.isGranted(permissionType, action);
    }

    @Restriction("channel.uploadReport")
    public boolean canUploadReport() {
        if (authorizationService.getAuthUser().getUserRole().getAccountRole() != INTERNAL) {
            return false;
        } else {
            return true;
        }
    }

    @Restriction("channel.downloadReport")
    public boolean canDownloadReport() {
        UserRole userRole = authorizationService.getAuthUser().getUserRole();
        switch (userRole.getAccountRole()) {
            case INTERNAL:
                return true;
            case ADVERTISER:
                AdvertisingAccount account = accountService.findAdvertisingUnchecked(authorizationService.getAuthUser().getAccountId());
                if (account.isSelfServiceFlag()) {
                    return false;
                } else {
                    return true;
                }
            default:
                return false;
        }
    }

    @Restriction("channel.downloadReport")
    public boolean canDownloadReport(Long accountId) {
        if (!entityRestrictions.isEntityAcessable(accountId)) {
            return false;
        }
        return canDownloadReport();
    }
}
