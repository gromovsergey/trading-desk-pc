package com.foros.action.birt;

import com.foros.action.Refreshable;
import com.foros.framework.CustomFileUploadInterceptor;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.fileman.AccountSizeExceededException;
import com.foros.session.fileman.FileManagerException;
import com.foros.session.fileman.FileSizeException;
import com.foros.util.BeanUtils;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;
import com.foros.util.UITimestamp;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.interceptor.RequestAware;

public class SaveBirtReportAction extends BirtReportActionSupport implements RequestAware, Refreshable {

    private Map<String, Object> request;

    @Restrict(restriction = "BirtReport.create")
    public String create() throws Exception {
        populateDependencies();
        String ret = preSave();
        if (SUCCESS.equals(ret)) {
            ret = save();
        }
        return ret;
    }

    @Restrict(restriction = "BirtReport.update", parameters = "#target.model.reportId")
    public String update() throws Exception {
        populateDependencies();
        String ret = preSave();
        if (SUCCESS.equals(ret)) {
            ret = save();
        }
        return ret;
    }

    private String preSave() {
        removeEmptyRecords();

        if (request.get(CustomFileUploadInterceptor.ATTRIBUTE_MAX_LENGTH_EXCEEDED) != null) {
            throw new FileSizeException();
        }

        return SUCCESS;
    }

    private void removeEmptyRecords() {
        for (int i = getModel().getPermissions().size() - 1; i >= 0; i--) {
            if (getModel().getPermissions().get(i) == null) {
                getModel().getPermissions().remove(i);
            }
        }
        for (int i = getModel().getUserRoles().size() - 1; i >= 0; i--) {
            if (StringUtil.isPropertyEmpty(getModel().getUserRoles().get(i))) {
                getModel().getUserRoles().remove(i);
            }
        }
    }

    private String save() throws Exception {
        // If maxLengthExceeded then Struts doesn't populate form values, so populate form
        BirtReportForm form = getModel();
        if (request.get(CustomFileUploadInterceptor.ATTRIBUTE_MAX_LENGTH_EXCEEDED) != null) {
            populateForm();
        }

        NumberFormat nf = CurrentUserSettingsHolder.getNumberFormat();

        BirtReport birtReport;
        if (StringUtil.isPropertyEmpty(form.getId())) {
            birtReport = new BirtReport();
        } else {
            birtReport = reportService.findForUpdate(Long.parseLong(form.getId()));
        }

        BeanUtils.copyProperties(birtReport, form, nf);

        List<UserRole> updatedUserRoles = new ArrayList<UserRole>();
        List<UserRole> removedUserRoles = new ArrayList<UserRole>();
        fillUserRoles(form, updatedUserRoles, removedUserRoles);

        File uploadedFile = form.getUploadedFile();
        long fileSize = 0;
        boolean updateFile = uploadedFile != null || StringUtil.isPropertyEmpty(form.getTemplateFile());
        InputStream is = null;
        try {
            if (uploadedFile != null) {
                fileSize = uploadedFile.length();
                if (fileSize > 0) {
                    is = new FileInputStream(uploadedFile);
                } else {
                    is = new ByteArrayInputStream(new byte[]{});
                }
            }
            if (StringUtil.isPropertyNotEmpty(form.getId())) {
                reportService.update(birtReport, updatedUserRoles, removedUserRoles, updateFile, is, fileSize);
            } else {
                birtReport.unregisterChange("id");
                form.setId(reportService.create(birtReport, updatedUserRoles, is, fileSize).toString());
            }
        } catch (AccountSizeExceededException e) {
            addFieldError("uploadedFile", getText("errors.file.accSizeExceeded"));
        } catch (FileManagerException e) {
            addFieldError("uploadedFile", getText("errors.upload", new String[]{getText("birtReports.file")}));
        } catch (ConstraintViolationException e) {
            if (!getFieldErrors().containsKey("uploadedFile") && uploadedFile != null) {
                addFieldError("uploadedFile", getText("errors.file.uploadAgain"));
            }
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }

        if (hasFieldErrors()) {
            return INPUT;
        }
        return SUCCESS;
    }

    private void fillUserRoles(BirtReportForm form, List<UserRole> updatedUserRoles, List<UserRole> removedUserRoles) {
        Map<Long, UserRole> prevUserRolesMap = new HashMap<Long, UserRole>();
        for (String prevUserRole : form.getUserRoles()) {
            Long userRoleId = PairUtil.fetchId(prevUserRole);
            String version = (PairUtil.parseIdNamePair(prevUserRole).getName());
            UserRole userRole = new UserRole(userRoleId);
            userRole.setVersion(new UITimestamp(version));
            prevUserRolesMap.put(userRoleId, userRole);
        }
        // fill selected user roles
        for (BirtReportForm.SecurityForm policyForm : form.getPermissions()) {
            String runPolicyId = policyForm.getRunPolicyId();
            String editPolicyId = policyForm.getEditPolicyId();

            UserRole userRole;
            if (StringUtil.isPropertyEmpty(policyForm.getUserRolePair())) {
                userRole = new UserRole(null);
            } else {
                Long userRoleId = PairUtil.fetchId(policyForm.getUserRolePair());
                if (prevUserRolesMap.containsKey(userRoleId)) {
                    userRole = prevUserRolesMap.get(userRoleId);
                    prevUserRolesMap.remove(userRoleId);
                } else {
                    userRole = new UserRole(userRoleId);
                    String version = (PairUtil.parseIdNamePair(policyForm.getUserRolePair()).getName());
                    userRole.setVersion(new UITimestamp(version));
                }
            }

            if (policyForm.isRun() && !policyForm.isRunGlobal()) {
                PolicyEntry policy = new PolicyEntry();

                if (StringUtil.isPropertyNotEmpty(runPolicyId)) {
                    policy.setId(Long.valueOf(runPolicyId));
                    policy.setVersion(policyForm.getRunPolicyVersion());
                }

                policy.setType("birt_report");
                policy.setParameter(form.getId());
                policy.setAction("run");
                policy.setUserRole(userRole);

                userRole.getPolicyEntries().add(policy);
            }

            if (policyForm.isEdit() && !policyForm.isEditGlobal()) {
                PolicyEntry policy = new PolicyEntry();

                if (StringUtil.isPropertyNotEmpty(editPolicyId)) {
                    policy.setId(Long.valueOf(editPolicyId));
                    policy.setVersion(policyForm.getEditPolicyVersion());
                }

                policy.setType("birt_report");
                policy.setParameter(form.getId());
                policy.setAction("edit");
                policy.setUserRole(userRole);

                userRole.getPolicyEntries().add(policy);
            }
            updatedUserRoles.add(userRole);
        }
        // fill removed user roles
        for (UserRole deleted : prevUserRolesMap.values()) {
            removedUserRoles.add(deleted);
        }
    }

    @Override
    public void needRefresh() {
        if (getModel().getId() != null) {
            populateForm();
        }
    }

    @Override
    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }
}
