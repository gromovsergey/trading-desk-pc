package com.foros.reporting.tools;

import org.joda.time.DateTime;

public class CancelQueryTO {
    private Long userId;
    private String userName;
    private String ip;
    private String description;
    private String id;
    private DateTime started;
    private boolean wasCancelCalled;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DateTime getStarted() {
        return started;
    }

    public void setStarted(DateTime started) {
        this.started = started;
    }

    public boolean isWasCancelCalled() {
        return wasCancelCalled;
    }

    public void setWasCancelCalled(boolean wasCancelCalled) {
        this.wasCancelCalled = wasCancelCalled;
    }
}
