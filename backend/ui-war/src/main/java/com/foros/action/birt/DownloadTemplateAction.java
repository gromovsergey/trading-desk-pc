package com.foros.action.birt;

import com.opensymphony.xwork2.Action;
import com.foros.action.download.DownloadFileActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.report.birt.BirtReport;
import com.foros.session.ServiceLocator;
import com.foros.session.birt.BirtReportService;

public class DownloadTemplateAction extends DownloadFileActionSupport {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ReadOnly
    public String download() {
        BirtReportService birtReportService = ServiceLocator.getInstance().lookup(BirtReportService.class);
        BirtReport report = birtReportService.findForUpdate(id);
        setTargetFile("birt_report_" + id + ".rptdesign");
        setContentSource(birtReportService.readTemplate(report));
        return Action.SUCCESS;
    }
}
