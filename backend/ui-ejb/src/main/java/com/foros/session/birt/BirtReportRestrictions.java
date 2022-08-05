package com.foros.session.birt;

import static com.foros.security.AccountRole.INTERNAL;
import com.foros.model.account.Account;
import com.foros.model.finance.Invoice;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.report.birt.BirtReportInstance;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.model.report.birt.BirtReportType;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.session.CurrentUserService;
import com.foros.session.UtilityService;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.admin.country.CountryRestrictions;
import com.foros.session.finance.InvoiceRestrictions;
import com.foros.session.security.UserService;

import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.validator.routines.LongValidator;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "birt_report", action = "run",    accountRoles = {INTERNAL}, parameterized = true),
        @Permission(objectType = "birt_report", action = "edit",   accountRoles = {INTERNAL}, parameterized = true),
        @Permission(objectType = "birt_report", action = "create", accountRoles = {INTERNAL})
})
public class BirtReportRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private UserService userService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CountryRestrictions countryRestrictions;

    @EJB
    private InvoiceRestrictions invoiceRestrictions;

    @EJB
    private UtilityService utilityService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Restriction
    public boolean canGet(Long reportId) {
        BirtReport report = utilityService.safeFind(BirtReport.class, reportId);
        if (report == null) {
            return false;
        }
        return restrictionService.isPermitted(report.getType().getRestriction(BirtReportType.RestrictionType.GET), reportId);
    }

    @Restriction
    public boolean canRun(Long reportId, Map<String, Object> params) {
        BirtReport report = utilityService.safeFind(BirtReport.class, reportId);
        if (report == null) {
            return false;
        }
        return restrictionService.isPermitted(report.getType().getRestriction(BirtReportType.RestrictionType.RUN), reportId, params);
    }

    @Restriction
    public boolean canUpdate(Long reportId) {
        BirtReport report = utilityService.safeFind(BirtReport.class, reportId);
        if (report == null) {
            return false;
        }
        return restrictionService.isPermitted(report.getType().getRestriction(BirtReportType.RestrictionType.UPDATE), reportId);
    }

    @Restriction
    public boolean canDefaultGet(Long reportId) {
        return permissionService.isGranted("birt_report", "run", reportId.toString());
    }

    @Restriction
    public boolean canDefaultRun(Long reportId, Map<String, Object> params) {
        return permissionService.isGranted("birt_report", "run", reportId.toString());
    }

    @Restriction
    public boolean canDefaultUpdate(Long reportId) {
        return permissionService.isGranted("birt_report", "edit", reportId.toString());
    }


    @Restriction
    public boolean canInvoiceGet(Long reportId) {
        Account account = userService.getMyUser().getAccount();

        if (!accountRestrictions.canView(account)) {
            return false;
        }

        if (account.getAccountType().getAccountRole() != INTERNAL) {
            BirtReport invoiceReport = account.getCountry().getInvoiceReport();

            if (invoiceReport == null) {
                return false;
            }

            return invoiceReport.getId().equals(reportId);
        }

        return true;
    }

    @Restriction
    public boolean canInvoiceRun(Long reportId, Map<String, Object> params) {
        Object invoiceId = params.get("invoiceId");


        if (!(invoiceId instanceof String)) {
            return false;
        }

        Long id = LongValidator.getInstance().validate((String) invoiceId);

        if (id == null) {
            return false;
        }

        return canInvoiceGet(reportId) && invoiceRestrictions.canView(em.find(Invoice.class, id));
    }

    @Restriction
    public boolean canInvoiceUpdate(Long reportId) {
        return countryRestrictions.canUpdate();
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("birt_report", "create");
    }

    @Restriction
    public boolean canView() {
        return currentUserService.isInternal();
    }

    @Restriction
    public boolean canViewAuditLog() {
        if (canCreate()) {
            return true;
        }

        UserRole role = userService.getMyUser().getRole();
        Map<PermissionDescriptor, Map<String, Long>> policy = permissionService.getPolicy(role.getId());
        for (PermissionDescriptor permission : policy.keySet()) {
            if (permission.getObjectType().contains("birt_report")) {
                return true;
            }
        }

        return false;
    }

    @Restriction
    public boolean canViewInstance(Long id) {
        if (!canView()) {
            return false;
        }

        BirtReportInstance instance = em.find(BirtReportInstance.class, id);

        return canViewInstance(instance);
    }

    private boolean canViewInstance(BirtReportInstance instance) {
        if (instance == null) {
            return false;
        }

        if (!canGet(instance.getReport().getId())) {
            return false;
        }

        return true;
    }

    @Restriction
    public boolean canViewSession(Long id) {
        if (!canView()) {
            return false;
        }

        BirtReportSession session = em.find(BirtReportSession.class, id);

        return canViewSession(session);
    }

    public boolean canViewSession(BirtReportSession session) {
        if (session == null) {
            return false;
        }

        User user = session.getUser();
        if (!user.getId().equals(userService.getMyUser().getId())) {
            return false;
        }

        BirtReport report = session.getReport();
        if (!canGet(report.getId())) {
            return false;
        }

        BirtReportInstance reportInstance = session.getBirtReportInstance();
        if (reportInstance != null && !canViewInstance(reportInstance)) {
            return false;
        }

        return true;
    }
}