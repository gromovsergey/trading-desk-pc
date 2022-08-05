package com.foros.action.reporting.invitations;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.invitations.InvitationsReportParameters;
import com.foros.session.reporting.invitations.InvitationsReportService;

import javax.ejb.EJB;


public class RunInvitationsReportingAction  extends RunReportingActionSupport<InvitationsReportParameters> {

    @EJB
    private InvitationsReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "invitations");
    }
}
