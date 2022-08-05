package app.programmatic.ui.common.model;

public class OwnedStatusableImpl implements OwnedStatusable {
    private Long accountId;
    private MajorDisplayStatus majorStatus;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setMajorStatus(MajorDisplayStatus majorStatus) {
        this.majorStatus = majorStatus;
    }

    public MajorDisplayStatus getMajorStatus() {
        return majorStatus;
    }
}
