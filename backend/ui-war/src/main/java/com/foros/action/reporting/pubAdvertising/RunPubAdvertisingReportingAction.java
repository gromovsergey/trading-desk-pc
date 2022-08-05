package com.foros.action.reporting.pubAdvertising;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.account.PublisherAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.account.AccountService;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.pubAdvertising.PubAdvertisingReportParameters;
import com.foros.session.reporting.pubAdvertising.PubAdvertisingReportService;

import javax.ejb.EJB;

public class RunPubAdvertisingReportingAction extends RunReportingActionSupport<PubAdvertisingReportParameters> {

    @EJB
    private PubAdvertisingReportService reportsService;

    @EJB
    private AccountService accountService;

    @Override
    @ReadOnly
    @Restrict(restriction = "Report.PubAdvertisingReport.run", parameters = "#target.model.accountId")
    public String execute() {
        PublisherAccount account = accountService.findPublisherAccount(parameters.getAccountId());
        parameters.setCountryCode(account.getCurrency().getCurrencyCode());
        parameters.setOutputType(getFormat());
        return safelyExecuteGeneric(reportsService, ReportType.PUB_ADVERTISING.getName());
    }
}

