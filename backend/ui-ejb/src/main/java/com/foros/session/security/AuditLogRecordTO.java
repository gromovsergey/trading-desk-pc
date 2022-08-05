package com.foros.session.security;

import com.foros.model.OracleJob;
import com.foros.model.Status;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.security.AccountRole;

import java.io.Serializable;
import org.joda.time.ReadablePartial;


public class AuditLogRecordTO implements Serializable {

    private long id;
    private ReadablePartial logDate;
    private ActionType actionType;
    private ObjectType objectType;
    private boolean success;
    private OracleJob financeJob;

    // object
    private Long objectId;
    private String objectName;

    // account
    private Long accountId;
    private AccountRole accountRole;
    private String accountName;
    private Status accountStatus;

    // user
    private Long userId;
    private String userLogin;
    private Status userStatus;

    // object owner
    private Long objectAccountId;
    private String objectAccountName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReadablePartial getLogDate() {
        return logDate;
    }

    public void setLogDate(ReadablePartial logDate) {
        this.logDate = logDate;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public OracleJob getFinanceJob() {
        return financeJob;
    }

    public void setFinanceJob(OracleJob financeJob) {
        this.financeJob = financeJob;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Status getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Status accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Status getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Status userStatus) {
        this.userStatus = userStatus;
    }

    public Long getObjectAccountId() {
        return objectAccountId;
    }

    public void setObjectAccountId(Long objectAccountId) {
        this.objectAccountId = objectAccountId;
    }

    public String getObjectAccountName() {
        return objectAccountName;
    }

    public void setObjectAccountName(String objectAccountName) {
        this.objectAccountName = objectAccountName;
    }
}
