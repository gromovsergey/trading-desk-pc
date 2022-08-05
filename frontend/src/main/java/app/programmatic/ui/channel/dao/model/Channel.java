package app.programmatic.ui.channel.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

public class Channel extends ChannelNameId {
    private Long accountId;
    private MajorDisplayStatus displayStatus;
    private MajorDisplayStatus accountDisplayStatus;
    private ChannelVisibility visibility;
    private ChannelType type;
    private Long uniqueUsers;
    private String localizedName;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public MajorDisplayStatus getAccountDisplayStatus() {
        return accountDisplayStatus;
    }

    public void setAccountDisplayStatus(MajorDisplayStatus accountDisplayStatus) {
        this.accountDisplayStatus = accountDisplayStatus;
    }

    public ChannelVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ChannelVisibility visibility) {
        this.visibility = visibility;
    }

    public Long getUniqueUsers() {
        return uniqueUsers;
    }

    public void setUniqueUsers(Long uniqueUsers) {
        this.uniqueUsers = uniqueUsers;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public String getLocalizedName() {
        return localizedName != null ? localizedName : getName();
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }
}
