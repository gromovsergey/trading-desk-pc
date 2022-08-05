package app.programmatic.ui.common.permission.dao.model;

public enum PermissionType {
    ADVERTISER_ADVERTISING_CHANNEL("advertiser_advertising_channel"),
    INTERNAL_ADVERTISING_CHANNEL("internal_advertising_channel"),
    ADVERTISER_ENTITY("advertiser_entity"),
    ADVERTISING_ACCOUNT("advertising_account"),
    AGENCY_ADVERTISER_ACCOUNT("agency_advertiser_account"),
    AGENT_REPORT("agent_report"),
    AUDIENCE_RESEARCH("audience_research"),
    API("API"),
    CAMPAIGN_CREDIT("campaignCredit"),
    REPORT_ADVERTISER("predefined_report_advertiser"),
    REPORT_CHANNEL("predefined_report_channel"),
    REPORT_CHANNEL_INVENTORY("predefined_report_channelInventory"),
    REPORT_CHANNEL_TRIGGERS("predefined_report_channelTriggers"),
    REPORT_CONVERSIONS("predefined_report_conversions"),
    REPORT_GENERAL_ADVERTISING("predefined_report_generalAdvertising"),
    REPORT_TEXT_ADVERTISING("predefined_report_textAdvertising"),
    REPORT_PUBLISHER("predefined_report_publisher"),
    REPORT_REFERRER("predefined_report_referrer");

    private final String storedValue;

    PermissionType(String storedValue) {
        this.storedValue = storedValue;
    }

    public String getStoredValue() {
        return storedValue;
    }

    public static PermissionType findByStoredValue(String value) {
        for (PermissionType type : PermissionType.values()) {
            if (type.getStoredValue().equals(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal Permission Type value: '" + value + "'");
    }
}
