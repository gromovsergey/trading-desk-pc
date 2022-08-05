package app.programmatic.ui.audienceresearch.view;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.sql.Timestamp;

public class AudienceResearchView {
    private Long id;
    private Long channelId;
    private String channelName;
    private MajorDisplayStatus displayStatus;
    private Timestamp startDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
}
