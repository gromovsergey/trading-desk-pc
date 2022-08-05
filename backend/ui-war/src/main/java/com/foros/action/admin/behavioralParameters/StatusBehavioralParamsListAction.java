package com.foros.action.admin.behavioralParameters;

import com.foros.action.BaseActionSupport;
import com.foros.session.admin.behavioralParameters.BehavioralParamsListService;

import javax.ejb.EJB;

public class StatusBehavioralParamsListAction extends BaseActionSupport {
    @EJB
    private BehavioralParamsListService behavioralParamsListService;

    private Long id;

    public String delete() {
        behavioralParamsListService.delete(getId());
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
