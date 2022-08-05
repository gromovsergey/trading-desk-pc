package com.foros.session.reporting.parameters;

import java.util.List;

public class TestReportParameters {

    private List<String> columns;
    private Long accountId;

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

}
