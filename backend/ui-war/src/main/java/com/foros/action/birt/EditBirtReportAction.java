package com.foros.action.birt;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.foros.framework.ReadOnly;
import com.foros.model.report.birt.BirtReport;
import com.foros.restriction.annotation.Restrict;

public class EditBirtReportAction extends BirtReportActionSupport implements ServletRequestAware {

    private HttpServletRequest request;
    private String messageKey;

    @ReadOnly
    @Restrict(restriction="BirtReport.create")
    public String create() {
        populateForm();
        populateDependencies();
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="BirtReport.update", parameters="#target.model.reportId")
    public String edit() {
        BirtReport report = populateForm();
        checkTemplate(report);
        populateDependencies();
        return SUCCESS;
    }

    private void checkTemplate(BirtReport report) {
        if (!reportService.checkTemplateExists(report)) {
            getModel().setTemplateFile(report.getTemplateFile());
        }
    }

    @ReadOnly
    public String showBirtError() throws Exception {
        populateReport();
        if (messageKey != null) {
            addFieldError("", getText(messageKey));
        }
        return SUCCESS;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
