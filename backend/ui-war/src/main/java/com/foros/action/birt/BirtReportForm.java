package com.foros.action.birt;

import com.foros.action.IdNameVersionForm;
import com.foros.util.StringUtil;
import com.foros.util.UITimestamp;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class BirtReportForm extends IdNameVersionForm<String> {
    public static class SecurityForm {
        private String userRolePair;
        private String userRoleName;
        private boolean run;
        private boolean edit;
        private String runPolicyId;
        private String editPolicyId;
        private UITimestamp runPolicyVersion;
        private UITimestamp editPolicyVersion;
        private boolean runGlobal;
        private boolean editGlobal;

        public String getUserRolePair() {
            return userRolePair;
        }

        public void setUserRolePair(String userRolePair) {
            this.userRolePair = userRolePair;
        }

        public String getUserRoleName() {
            return userRoleName;
        }

        public void setUserRoleName(String userRoleName) {
            this.userRoleName = userRoleName;
        }

        public boolean isRun() {
            return run;
        }

        public void setRun(boolean run) {
            this.run = run;
        }

        public boolean isEdit() {
            return edit;
        }

        public void setEdit(boolean edit) {
            this.edit = edit;
        }

        public String getRunPolicyId() {
            return runPolicyId;
        }

        public void setRunPolicyId(String runPolicyId) {
            this.runPolicyId = runPolicyId;
        }

        public String getEditPolicyId() {
            return editPolicyId;
        }

        public void setEditPolicyId(String editPolicyId) {
            this.editPolicyId = editPolicyId;
        }

        public UITimestamp getRunPolicyVersion() {
            if (runPolicyVersion == null) {
                runPolicyVersion = new UITimestamp((System.currentTimeMillis()));
            }

            return runPolicyVersion;
        }

        public void setRunPolicyVersion(UITimestamp runPolicyVersion) {
            if (runPolicyVersion == null) {
                this.runPolicyVersion = new UITimestamp((System.currentTimeMillis()));
            } else {
                this.runPolicyVersion = runPolicyVersion;
            }
        }

        public UITimestamp getEditPolicyVersion() {
            if (editPolicyVersion == null) {
                editPolicyVersion = new UITimestamp((System.currentTimeMillis()));
            }

            return editPolicyVersion;
        }

        public void setEditPolicyVersion(UITimestamp editPolicyVersion) {
            if (editPolicyVersion == null) {
                this.editPolicyVersion = new UITimestamp((System.currentTimeMillis()));
            } else {
                this.editPolicyVersion = editPolicyVersion;
            }
        }

        public boolean isEditGlobal() {
            return editGlobal;
        }

        public void setEditGlobal(boolean editGlobal) {
            this.editGlobal = editGlobal;
        }

        public boolean isRunGlobal() {
            return runGlobal;
        }

        public void setRunGlobal(boolean runGlobal) {
            this.runGlobal = runGlobal;
        }

        public boolean hasGlobalOpeation() {
            return isRunGlobal() || isEditGlobal();
        }
    }

    public static class SecurityFormComparator implements Comparator<SecurityForm> {
        @Override
        public int compare(SecurityForm sf1, SecurityForm sf2) {
            if (sf1.hasGlobalOpeation() && !sf2.hasGlobalOpeation()) {
                return -1;
            } else if (sf2.hasGlobalOpeation() && !sf1.hasGlobalOpeation()) {
                return 1;
            } else {
                return sf1.getUserRoleName().compareTo(sf2.getUserRoleName());
            }
        }
    }

    private File uploadedFile = null;
    private String templateFile;
    private List<SecurityForm> permissions = new LinkedList<SecurityForm>();
    private List<String> userRoles = new LinkedList<String>();

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    protected boolean isTemplateUploaded() {
        return uploadedFile != null && uploadedFile.length() != 0;
    }

    public List<SecurityForm> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SecurityForm> permissions) {
        this.permissions = permissions;
    }

    public File getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(File uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

    public Long getReportId() {
        return StringUtil.toLong(getId());
    }
}
