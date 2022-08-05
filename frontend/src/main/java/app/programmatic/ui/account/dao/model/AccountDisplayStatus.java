package app.programmatic.ui.account.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

public enum AccountDisplayStatus {
    LIVE(1, MajorDisplayStatus.LIVE),
    NOT_LIVE(2, MajorDisplayStatus.NOT_LIVE, "overdraft"),
    INACTIVE(3, MajorDisplayStatus.INACTIVE),
    DELETED(4, MajorDisplayStatus.DELETED),
    NO_BILLING_CONTACT(5, MajorDisplayStatus.NOT_LIVE, "noBillingContact");

    private int id;
    private MajorDisplayStatus majorStatus;
    private String descriptionKey;

    AccountDisplayStatus(int id, MajorDisplayStatus majorStatus, String descriptionKey) {
        this.id = id;
        this.majorStatus = majorStatus;
        this.descriptionKey = descriptionKey;
    }

    AccountDisplayStatus(int id, MajorDisplayStatus majorStatus) {
        this(id, majorStatus, "");
    }

    public int getId() {
        return id;
    }

    public MajorDisplayStatus getMajorStatus() {
        return majorStatus;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public static AccountDisplayStatus valueOf(int displayStatusId) {
        for (AccountDisplayStatus status : AccountDisplayStatus.values()) {
            if (status.getId() == displayStatusId) {
                return status;
            }
        }
        throw new IllegalArgumentException("There is no display status for account with display status id " + displayStatusId);
    }

    public static AccountDisplayStatus findByName(String name) {
        for (AccountDisplayStatus status : AccountDisplayStatus.values()) {
            if ((status.getMajorStatus().toString() + "|" + status.getDescriptionKey()).equals(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("There is no display status for name " + name);
    }
}
