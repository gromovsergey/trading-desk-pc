package com.foros.session.reporting.invitations;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;

public class InvitationsReportParameters extends DatedReportParameters {

    @RequiredConstraint
    private Long accountId;

    private boolean showBrowserFamilies;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public boolean getShowBrowserFamilies() {
        return showBrowserFamilies;
    }

    public void setShowBrowserFamilies(boolean showBrowserFamilies) {
        this.showBrowserFamilies = showBrowserFamilies;
    }
}
