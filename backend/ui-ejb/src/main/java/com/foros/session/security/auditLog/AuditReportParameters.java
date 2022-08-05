package com.foros.session.security.auditLog;

import com.foros.model.security.ActionType;
import com.foros.model.security.ResultType;

import java.util.List;
import org.joda.time.LocalDateTime;

public class AuditReportParameters {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private List<Long> accountRoleIds;
    private String accountName;
    private String email;
    private List<Long> objectTypeIds;
    private ActionType actionType;
    private ResultType resultType;
    private Long page;
    private boolean oracleJobsOnly = false;

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public List<Long> getAccountRoleIds() {
        return accountRoleIds;
    }

    public void setAccountRoleIds(List<Long> accountRoleIds) {
        this.accountRoleIds = accountRoleIds;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getObjectTypeIds() {
        return objectTypeIds;
    }

    public void setObjectTypeIds(List<Long> objectTypeIds) {
        this.objectTypeIds = objectTypeIds;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public boolean isOracleJobsOnly() {
        return oracleJobsOnly;
    }

    public void setOracleJobsOnly(boolean oracleJobsOnly) {
        this.oracleJobsOnly = oracleJobsOnly;
    }
}
