package app.programmatic.ui.reporting.restriction;

import app.programmatic.ui.account.dao.model.Account;
import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.dao.model.PublisherAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.permission.service.PermissionService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;
import app.programmatic.ui.common.restriction.service.EntityRestrictions;

import static app.programmatic.ui.common.permission.dao.model.PermissionAction.RUN;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.*;

@Service
@Restrictions
public class ReportRestrictions {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private EntityRestrictions entityRestrictions;

    @Restriction("report.publisher")
    public boolean canRunPublisher(Long accountId) {
        if (!permissionService.isGranted(REPORT_PUBLISHER, RUN)) {
            return false;
        }

        PublisherAccount account = accountService.findPublisherUnchecked(accountId);
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("report.publisher")
    public boolean canRunPublisher() {
        return permissionService.isGranted(REPORT_PUBLISHER, RUN);
    }

    @Restriction("report.generalAdvertising")
    public boolean canRunGeneralAdvertising(Long accountId) {
        if (!permissionService.isGranted(REPORT_GENERAL_ADVERTISING, RUN)) {
            return false;
        }

        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("report.conversions")
    public boolean canRunConversions(Long accountId) {
        if (!permissionService.isGranted(REPORT_CONVERSIONS, RUN)) {
            return false;
        }

        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("report.referrer")
    public boolean canRunReferrer() {
        return permissionService.isGranted(REPORT_REFERRER, RUN);
    }

    @Restriction("report.referrer")
    public boolean canRunReferrer(Long accountId) {
        if (!permissionService.isGranted(REPORT_REFERRER, RUN)) {
            return false;
        }

        PublisherAccount account = accountService.findPublisherUnchecked(accountId);
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("report.detailed")
    public boolean canRunDetailed() {
        User user = authorizationService.getAuthUser();
        UserRole userRole = user.getUserRole();

        return userRole.getAccountRole() == AccountRole.INTERNAL;
    }

    @Restriction("report.detailed")
    public boolean canRunDetailed(Long accountId) {
        return canRunDetailed();
    }

    @Restriction("report.segments")
    public boolean canRunSegments() {
        return true; // ToDo: implement restriction
    }

    @Restriction("report.segments")
    public boolean canRunSegments(Long accountId) {
        return canRunSegments();
    }
}
