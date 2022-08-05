package com.foros.session.reporting.dashboard;

import com.foros.validation.constraint.RequiredConstraint;

public class AccountDashboardParameters extends DashboardParameters {

    @RequiredConstraint
    private Long accountId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
