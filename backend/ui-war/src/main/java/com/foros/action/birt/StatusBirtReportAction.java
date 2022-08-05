package com.foros.action.birt;

import com.foros.action.BaseActionSupport;
import com.foros.session.birt.BirtReportService;

import javax.ejb.EJB;

public class StatusBirtReportAction extends BaseActionSupport {

    @EJB
    private BirtReportService birtReportService;

    private Long id;

    public String delete() {
        birtReportService.delete(getId());
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
