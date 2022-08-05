package com.foros.action.support;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.support.DBJobsExecutorService;

import javax.ejb.EJB;

public class DBJobsExecutorAction extends BaseActionSupport {
    @EJB
    private DBJobsExecutorService dbJobsExecutorService;

    @ReadOnly
    public String main() throws Exception {
        return SUCCESS;
    }

    public String checkThresholdChannelByUsers() {
        dbJobsExecutorService.checkThresholdChannelByUsers();
        return SUCCESS;
    }

    public String checkPendingInactivation() {
        dbJobsExecutorService.checkPendingInactivation();
        return SUCCESS;
    }

    @Deprecated
    public String checkBillingDate() {
        dbJobsExecutorService.checkBillingDate();
        return SUCCESS;
    }

    public String calcCTR() {
        dbJobsExecutorService.calcCTR();
        return SUCCESS;
    }
}
