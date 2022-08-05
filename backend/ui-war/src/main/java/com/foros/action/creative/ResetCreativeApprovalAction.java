package com.foros.action.creative;

import com.foros.action.BaseActionSupport;
import com.foros.model.creative.Creative;
import com.foros.session.creative.CreativeService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;


public class ResetCreativeApprovalAction extends BaseActionSupport implements ModelDriven<Creative> {
    @EJB
    private CreativeService creativeService;

    private Creative creative = new Creative();

    public String reset() {
        creativeService.resetRejectedCreativeExclusions(creative.getId());
        return SUCCESS;
    }

    @Override
    public Creative getModel() {
        return creative;
    }
}
