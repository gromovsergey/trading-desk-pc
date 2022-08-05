package com.foros.action.account;

import com.foros.model.DisplayStatus;
import com.foros.model.account.Account;

public enum AccountSearchStatus {
    ALL("displaystatus.major.all", null),
    ALL_HIDE_DELETED("displaystatus.major.all",
            new DisplayStatus[]{Account.LIVE, Account.NOT_LIVE, Account.INACTIVE, Account.NO_BILLING_CONTACT}),
    ALL_BUT_DELETED("displaystatus.major.all_but_deleted",
            new DisplayStatus[]{Account.LIVE, Account.NOT_LIVE, Account.INACTIVE, Account.NO_BILLING_CONTACT}),
    LIVE("displaystatus.major.live", new DisplayStatus[]{Account.LIVE}),
    NOT_LIVE("displaystatus.major.not_live", new DisplayStatus[]{Account.NOT_LIVE, Account.NO_BILLING_CONTACT}),
    INACTIVE("displaystatus.major.inactive", new DisplayStatus[]{Account.INACTIVE}),
    DELETED("displaystatus.major.deleted", new DisplayStatus[]{Account.DELETED});

    private final String description;
    private final DisplayStatus[] displayStatuses;

    private AccountSearchStatus(String description, DisplayStatus[] displayStatuses) {
        this.description = description;
        this.displayStatuses = displayStatuses;
    }

    public String getDescription() {
        return description;
    }

    public DisplayStatus[] getDisplayStatuses() {
        return displayStatuses;
    }

    public String getName() {
        return this.name();
    }
}
