package app.programmatic.ui.restriction.model;

public enum RestrictionParam {
    NOT_REQUIRED(null),
    ID(null, true),
    ADVERTISER("Advertiser"),
    ACCOUNT_ID("com.foros.model.account.Account", true),
    AGENCY_ACCOUNT_ID("com.foros.model.account.AgencyAccount", true),
    ADVERTISER_ACCOUNT_ID("com.foros.model.account.AdvertiserAccount", true),
    CCG_ID("com.foros.model.campaign.CampaignCreativeGroup", true),
    CAMPAIGN_ID("com.foros.model.campaign.Campaign", true),
    CHANNEL_ID("com.foros.model.channel.Channel", true),
    CONVERSION_ID("com.foros.model.action.Action", true),
    CREATIVE_ID("com.foros.model.creative.Creative", true),
    USER_ID("com.foros.model.security.User", true)
    ;

    private String forosName;
    private boolean idRequired;

    RestrictionParam(String forosName, boolean idRequired) {
        this.forosName = forosName;
        this.idRequired = idRequired;
    }

    RestrictionParam(String forosName) {
        this(forosName, false);
    }

    public String getForosName() {
        return forosName;
    }

    public boolean isIdRequired() {
        return idRequired;
    }
}
