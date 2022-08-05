package com.foros.session.reporting;

public enum ReportType {
    CUSTOM(1, "custom"),
    DISPLAY_ADVERTISING(2, "advertiser"),
    TEXT_ADVERTISING(3, "textAdvertising"),
    PUBLISHER(4, "publisher"),
    ISP(5, "ISP"),
    WEBWISE(6, "webwise"),
    ISP_EARNINGS(7, "ISPEarnings"), // This report no longer exists but we need it to show audit report.
    PUBLISHER_EARNINGS(8, "publisherEarnings"), // This report no longer exists but we need it to show audit report.
    INVENTORY_ESTIMATION(9, "inventoryEstimation"),
    REFERRER(10, "referrer"),
    CHANNEL_TRIGGERS(11, "channelTriggers"),
    CHANNEL_SITES(12, "channelSites"),
    SITE_CHANNELS(13, "siteChannels"),
    AUDIT(14, "audit"),
    CONVERSIONS(15, "conversions"),
    CONVERSION_PIXELS(16, "conversionPixels"),
    CHANNEL_USAGE(17, "channelUsage"),
    CHANNEL(18, "channel"),
    CHANNEL_INVENTORY(19, "channelInventory"),
    CAMPAIGN_ALLOCATION_HISTORY(20, "campaignAllocationHistory"),
    USER_AGENTS(21, "userAgents"),
    INVITATIONS(22, "invitations"),
    ACTIVE_ADVERTISERS(23, "activeAdvertisers"),
    CAMPAIGN_OVERVIEW(24, "campaignOverview"),
    PUBLISHER_OVERVIEW(25, "publisherOverview"),
    PROFILING(26, "profiling"),
    WATERFALL(27, "waterfall"),
    SELECTION_FAILURES(28, "selectionFailures"),
    SELECTION_FAILURES_TREND(29, "selectionFailuresTrend"),
    PUB_ADVERTISING(30, "pubAdvertising"),
    OLAPCUSTOM(31, "olapcustom"), // This report no longer exists but we need it to show audit report.
    VIDEO_ADVERTISING(32, "videoAdvertising"), // This report no longer exists but we need it to show audit report.
    GENERAL_ADVERTISING(33, "generalAdvertising"),
    ;

    private long id;
    private String name;

    private ReportType(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static ReportType byId(Long id) {
        for (ReportType reportType : values()) {
            if (reportType.getId() == id) {
                return reportType;
            }
        }

        throw new IllegalArgumentException("Illegal id given: '" + id + "'");
    }
}
