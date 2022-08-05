package com.foros.reporting.tools.query.parameters.usertype;

public class PostgreTriggerIdQaUserType {
    private Integer triggerId;
    private char qaStatus;

    public PostgreTriggerIdQaUserType(Integer triggerId, char qaStatus) {
        this.triggerId = triggerId;
        this.qaStatus = qaStatus;
    }

    @Override
    public String toString() {
        return "(" + triggerId + "," + qaStatus + ")";
    }
}
