package app.programmatic.ui.user.dao.model;

public enum InternalAccessType {
    U("User Account"),
    M("Multiple Accounts"),
    A("All Accounts");

    private String description;

    InternalAccessType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
