package com.foros.session.birt;

import com.foros.model.report.birt.BirtReport;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class BirtReportValidations {

    private final static int MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final static int FIRST_UNSUPPORTED_REPORT_ID = 1000000;

    @EJB
    private UserRoleService userRoleService;

    @EJB
    protected BirtReportService reportService;

    @Validation
    public void validateCreate(ValidationContext validationContext,
                               @ValidateBean(ValidationMode.CREATE) BirtReport report,
                               List<UserRole> userRoles, InputStream is, Long fileSize) {
        ValidationContext context = validationContext.createSubContext(report);

        validateRoles(context, userRoles, new LinkedList<UserRole>(),  null);
        validateFile(context, is, fileSize);
    }

    @Validation
    public void validateUpdate(ValidationContext validationContext,
                               @ValidateBean(ValidationMode.UPDATE) BirtReport report,
                               List<UserRole> updatedUserRoles, List<UserRole> removedUserRoles,
                               Boolean updateFile, InputStream is, Long fileSize) {
        ValidationContext context = validationContext.createSubContext(report);

        validateRoles(context, updatedUserRoles, removedUserRoles, report.getId());
        if (updateFile) {
            validateFile(context, is, fileSize);
        }

        BirtReport existing = reportService.findForUpdate(report.getId());
        if (existing.getId() < FIRST_UNSUPPORTED_REPORT_ID && !existing.getName().equals(report.getName())) {
            context.addConstraintViolation("report.supported.error");
        }
    }

    private void validateFile(ValidationContext context, InputStream is, Long fileSize) {
        if (is == null) {
            context.addConstraintViolation("errors.field.required")
                .withPath("uploadedFile");
            return;
        }
        if (fileSize == 0) {
            context.addConstraintViolation("errors.fileEmptyNotExist")
                .withPath("uploadedFile")
                .withParameters("{birtReports.file}");
            return;
        }
        if (fileSize > MAX_FILE_SIZE) {
            context.addConstraintViolation("errors.file.sizeExceeded")
                .withPath("uploadedFile");
            return;
        }
    }

    private void validateRoles(ValidationContext context, List<UserRole> updatedUserRoles, List<UserRole> removedUserRoles, Long birtReportId) {
        Set<Long> userRoleIds = new HashSet<Long>();
        for (int i = 0; i < updatedUserRoles.size(); i++) {
            UserRole role = updatedUserRoles.get(i);
            if (role.getId() == null) {
                context.addConstraintViolation("errors.field.required")
                    .withPath("permissions[" + i + "].role.id");
                continue;
            }
            if (userRoleIds.contains(role.getId())) {
                context.addConstraintViolation("UserRole.errors.duplicateRole")
                    .withPath("permissions[" + i + "].role.id")
                    .withParameters(role.getName());
                continue;
            }
            userRoleIds.add(role.getId());
            if (!validateRole(role, birtReportId, false)) {
                context.addConstraintViolation("UserRole.errors.invalidPermission")
                    .withPath("permissions[" + i + "].role.id");
                continue;
            }
        }
        for (UserRole removed: removedUserRoles) {
            if (userRoleIds.contains(removed.getId())) {
                context.addConstraintViolation("UserRole.errors.duplicateRole")
                    .withPath("removedpermission");
                continue;
            }
            if (!validateRole(removed, birtReportId, true)) {
                context.addConstraintViolation("UserRole.errors.invalidPermission")
                    .withPath("removedpermission");
                continue;
            }
        }
    }

    private boolean validateRole(UserRole role, Long birtReportId, boolean isRemoved) {
        boolean isRunGlobal = false;
        boolean isEditGlobal = false;
        for (PolicyEntry policy: userRoleService.findPolicyEntries(role.getId())) {
            if ("birt_report".equals(policy.getType())) {
                if ("run".equals(policy.getAction()) && StringUtil.isPropertyEmpty(policy.getParameter())) {
                    isRunGlobal = true;
                }
                if ("edit".equals(policy.getAction()) && StringUtil.isPropertyEmpty(policy.getParameter())) {
                    isEditGlobal = true;
                }
            }
        }
        boolean isRun = false;
        boolean isEdit = false;
        for (PolicyEntry policy: role.getPolicyEntries()) {
            if (!"birt_report".equals(policy.getType())) {
                return false;
            }
            if (birtReportId == null && !StringUtil.isPropertyEmpty(policy.getParameter())) {
                return false;
            }
            if (birtReportId != null && !birtReportId.toString().equals(policy.getParameter())) {
                return false;
            }
            if ("run".equals(policy.getAction())) {
                isRun = true;
            }
            else
                if ("edit".equals(policy.getAction())) {
                    isEdit = true;
                }
                else {
                    return false;
                }
        }
        boolean isEmpty = !(isRun || isRunGlobal || isEdit || isEditGlobal);
        if (isRemoved && !isEmpty || !isRemoved && isEmpty) {
            return false;
        }
        return true;
    }
}
