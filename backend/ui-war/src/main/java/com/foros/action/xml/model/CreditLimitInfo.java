package com.foros.action.xml.model;

public class CreditLimitInfo {
    private String status;
    private String maxCreditLimit;
    private String message;

    public CreditLimitInfo(String status) {
        this(status, null, null);
    }

    public CreditLimitInfo(String status, String maxCreditLimit, String message) {
        this.status = status;
        this.maxCreditLimit = maxCreditLimit;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMaxCreditLimit() {
        return maxCreditLimit;
    }

    public String getMessage() {
        return message;
    }
}
