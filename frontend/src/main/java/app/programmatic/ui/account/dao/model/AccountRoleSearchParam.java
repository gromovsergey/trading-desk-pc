package app.programmatic.ui.account.dao.model;

public class AccountRoleSearchParam {
    private final AccountRoleParam roleId;
    private final String name;

    public AccountRoleSearchParam(AccountRoleParam roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    public AccountRoleParam getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }
}
