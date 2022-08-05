package app.programmatic.ui.account.dao.model;

import java.util.EnumSet;

public enum AccountDisplayStatusParam {
    ALL(EnumSet.of(AccountDisplayStatus.LIVE, AccountDisplayStatus.NOT_LIVE, AccountDisplayStatus.INACTIVE)),
    LIVE(EnumSet.of(AccountDisplayStatus.LIVE)),
    NOT_LIVE(EnumSet.of(AccountDisplayStatus.NOT_LIVE)),
    INACTIVE(EnumSet.of(AccountDisplayStatus.INACTIVE));

    private final EnumSet<AccountDisplayStatus> displayStatuses;

    AccountDisplayStatusParam(EnumSet<AccountDisplayStatus> displayStatuses) {
        this.displayStatuses = displayStatuses;
    }

    public EnumSet<AccountDisplayStatus> getDisplayStatuses() {
        return displayStatuses;
    }
}
