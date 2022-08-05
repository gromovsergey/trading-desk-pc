package com.foros.session.security;

import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.security.User;

public class UserTO {
    private Long userId;
    private String userEmail;
    private DisplayStatus userDisplayStatus;

    private Long accountId;
    private String accountName;
    private DisplayStatus accountDisplayStatus;
    private String countryCode;

    private Long userRoleId;
    private String userRoleName;

    public UserTO(Long userId, String userEmail, char userStatus,
                  Long accountId, String accountName, Long accountDisplayStatusId,
                  String countryCode,
                  Long userRoleId, String userRoleName) {

        this.userId = userId;
        this.userEmail = userEmail;
        this.userDisplayStatus = User.getDisplayStatus(Status.valueOf(userStatus));

        this.accountId = accountId;
        this.accountName = accountName;
        this.accountDisplayStatus = Account.getDisplayStatus(accountDisplayStatusId);

        this.countryCode = countryCode;

        this.userRoleId = userRoleId;
        this.userRoleName = userRoleName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public DisplayStatus getUserDisplayStatus() {
        return userDisplayStatus;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public DisplayStatus getAccountDisplayStatus() {
        return accountDisplayStatus;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Long getUserRoleId() {
        return userRoleId;
    }

    public String getUserRoleName() {
        return userRoleName;
    }
}
