package app.programmatic.ui.changetrack.dao.model;

public enum TableName {
    USER("users"),
    USER_ROLE("userrole"),
    USER_CREDENTIAL("usercredentials"),
    CAMPAIGN("campaign");

    private final String storedName;

    TableName(String storedName) {
        this.storedName = storedName;
    }

    public String getStoredName() {
        return storedName;
    }
}
