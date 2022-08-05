package com.foros.session.channel;

public class ChannelDataTO {
    private Long channelId;
    private char triggersStatus;
    private Integer urlTriggersCount;
    private String auditLog;

    public ChannelDataTO() {
    }

    public ChannelDataTO(Long channelId, char triggersStatus,
            Integer urlTriggersCount, String auditLog) {
        this.channelId = channelId;
        this.triggersStatus = triggersStatus;
        this.urlTriggersCount = urlTriggersCount;
        this.auditLog = auditLog;
    }

    public String getAuditLog() {
        return auditLog;
    }

    public Long getChannelId() {
        return channelId;
    }

    public char getTriggersStatus() {
        return triggersStatus;
    }

    public Integer getUrlTriggersCount() {
        return urlTriggersCount;
    }

    public void setAuditLog(String auditLog) {
		this.auditLog = auditLog;
	}

	public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public void setTriggersStatus(char triggersStatus) {
        this.triggersStatus = triggersStatus;
    }

    public void setUrlTriggersCount(Integer urlTriggersCount) {
        this.urlTriggersCount = urlTriggersCount;
    }

    @Override
    public String toString() {
        return "ChannelDataTO [channelId=" + channelId + ", triggersStatus="
                + triggersStatus + ", urlTriggersCount=" + urlTriggersCount
                + ", auditLog=" + auditLog + "]";
    }
}
