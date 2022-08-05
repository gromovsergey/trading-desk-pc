package app.programmatic.ui.account.dao.model;

public enum AccountRole {
    INTERNAL(0),
    ADVERTISER(1),
    PUBLISHER(2),
    ISP(3),
    AGENCY(4),
    CMP(5);

    private int id;

    AccountRole(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static AccountRole valueOf(int index) {
        return values()[index];
    }
}
