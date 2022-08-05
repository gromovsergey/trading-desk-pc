package com.foros.action.admin.behavioralParameters;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.session.admin.behavioralParameters.BehavioralParamsListService;

import javax.ejb.EJB;
import java.util.List;

public class ListBehavioralParamsListAction extends BaseActionSupport {
    @EJB
    protected BehavioralParamsListService behavioralParamsListService;

    private List<BehavioralParametersList> behavioralParams;

    @ReadOnly
    public String list() {
        behavioralParams = behavioralParamsListService.findAll();
        return SUCCESS;
    }

    public List<BehavioralParametersList> getBehavioralParams() {
        return behavioralParams;
    }
}
