package com.foros.model;

public enum OracleJob {
    AP01_ACCOUNT("AP01-Customer Data (Account)"),
    AP05("AP05-Supplier Financial Data"),
    AP10("AP10-Supplier Invoice"),
    AP15("AP15-Payables Status"),
    AR01("AR01-Customer Data (Account)"),
    AR05("AR05-Customer Financial Data"),
    AR10("AR10-Customer Invoice"),
    AR20("AR20-Customer Invoice Status"),
    AR25("AR25-On Account Credits"),
    GL01("GL01-FOROS Journal"),
    BILLING("Billing"),
    AP01_ADDRESS("AP01-Customer Data (Address)"),
    AP01_USER("AP01-Customer Data (User)"),
    AR01_ADDRESS("AR01-Customer Data (Address)"),
    AR01_USER("AR01-Customer Data (User)"),
    EXPIRE_CAMPAIGNS("Expire Campaigns"),
    CHANNEL_QA_SERVICE("Channel QA Service"),
    STATISTIC_UPDATE("Statistic Update"),
    TRIGGER_QA("Trigger QA"),
    DISCOVER_AUTO_CHANNEL("Discover Auto Channel"),
    CHECK_CAMPAIGN_START("Check Campaign Start"),
    CMP_CHANNEL_INACTIVATION("CMP Channel Inactivation"),
    THRESHOLD_FOR_ADV_CHANNELS("Threshold for Adv channels"),
    BULK_UPDATE_OF_DISPLAY_STATUS("Bulk Update Of Display Status"),
    RESET_MESSAGE_SENT_COUNT("Reset Message Sent Count"),
    AUTOGENERATED_CHANNELS_CLEANER("Delete auto-generated channels");

    OracleJob(String name) {
        this.name = name;
    }

    private String name;

    public static OracleJob findByOrdinal(Integer ordinal) {
        if (ordinal != null) {
            for (OracleJob oracleJob : values()) {
                if (oracleJob.ordinal() == ordinal) {
                    return oracleJob;
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}