package app.programmatic.ui.account.dao.model;

import java.util.List;

public class AdditionalSearchParams {
    private List<CountrySearchParam> countries;
    private List<StatusSearchParam> displayStatuses;
    private List<AccountRoleSearchParam> accountRoles;

    public List<CountrySearchParam> getCountries() {
        return countries;
    }

    public void setCountries(List<CountrySearchParam> countries) {
        this.countries = countries;
    }

    public List<StatusSearchParam> getDisplayStatuses() {
        return displayStatuses;
    }

    public void setDisplayStatuses(List<StatusSearchParam> displayStatuses) {
        this.displayStatuses = displayStatuses;
    }

    public List<AccountRoleSearchParam> getAccountRoles() {
        return accountRoles;
    }

    public void setAccountRoles(List<AccountRoleSearchParam> accountRoles) {
        this.accountRoles = accountRoles;
    }
}
