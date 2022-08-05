package app.programmatic.ui.user.dao.model;

import app.programmatic.ui.account.dao.model.AccountRole;

import static app.programmatic.ui.account.dao.model.AccountRole.ADVERTISER;
import static app.programmatic.ui.account.dao.model.AccountRole.AGENCY;

public enum UserRoleType {

    ADMIN("External Administrator", AGENCY),
    STANDARD("Full Access", AGENCY),
    READ_ONLY("Read-only", AGENCY),
    READ_ONLY_PLUS("Read-only Plus", AGENCY),
    ADV_ADMIN("Advertiser External Administrator", ADVERTISER),
    ADV_STANDART("Advertiser Full Access", ADVERTISER),
    ADV_READ_ONLY("Advertiser Read-only", ADVERTISER);

    UserRoleType(String name, AccountRole accountRole) {
        this.name = name;
        this.accountRole = accountRole;
    }

    private String name;
    private AccountRole accountRole;

    public String getName() {
        return name;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }
}
