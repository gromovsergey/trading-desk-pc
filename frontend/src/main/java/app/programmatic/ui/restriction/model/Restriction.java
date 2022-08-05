package app.programmatic.ui.restriction.model;

import static app.programmatic.ui.restriction.model.RestrictionParam.*;


public enum Restriction {
    SEARCH_ADVERTISER_ACCOUNTS("Context.switch", ADVERTISER),
    SEARCH_ADVERTISER_IN_AGENCY_ACCOUNTS("AdvertisingAccount.viewList", NOT_REQUIRED),
    VIEW_ACCOUNT("Account.view", ACCOUNT_ID),
    UPDATE_ACCOUNT("Account.update", ACCOUNT_ID),
    ACTIVATE_ACCOUNT("Account.activate", ACCOUNT_ID),
    INACTIVATE_ACCOUNT("Account.inactivate", ACCOUNT_ID),
    CREATE_ADVERTISER_IN_AGENCY("AgencyAdvertiserAccount.create", AGENCY_ACCOUNT_ID),
    UPDATE_ADVERTISER_IN_AGENCY("AgencyAdvertiserAccount.update", ADVERTISER_ACCOUNT_ID),
    VIEW_ADVERTISING_CHANNELS("AdvertisingChannel.view", ACCOUNT_ID),
    VIEW_ADVERTISING_CHANNEL("AdvertisingChannel.view", CHANNEL_ID),
    CREATE_ADVERTISING_CHANNEL("AdvertisingChannel.create", ACCOUNT_ID),
    UPDATE_ADVERTISING_CHANNEL("AdvertisingChannel.update", CHANNEL_ID),
    VIEW_CCG("AdvertiserEntity.view", CCG_ID),
    CREATE_CCG("AdvertiserEntity.create", CAMPAIGN_ID),
    UPDATE_CCG("AdvertiserEntity.update", CCG_ID),
    ACTIVATE_CCG("AdvertiserEntity.activate", CCG_ID),
    INACTIVATE_CCG("AdvertiserEntity.inactivate", CCG_ID),
    VIEW_CAMPAIGN("AdvertiserEntity.view", CAMPAIGN_ID),
    CREATE_CAMPAIGN("AdvertiserEntity.create", ACCOUNT_ID),
    UPDATE_CAMPAIGN("AdvertiserEntity.update", CAMPAIGN_ID),
    ACTIVATE_CAMPAIGN("AdvertiserEntity.activate", CAMPAIGN_ID),
    INACTIVATE_CAMPAIGN("AdvertiserEntity.inactivate", CAMPAIGN_ID),
    CREATE_CONVERSION("AdvertiserEntity.create", ACCOUNT_ID),
    UPDATE_CONVERSION("AdvertiserEntity.update", CONVERSION_ID),
    CREATE_CREATIVE("AdvertiserEntity.create", ACCOUNT_ID),
    UPDATE_CREATIVE("AdvertiserEntity.update", CREATIVE_ID),
    RUN_GENERAL_ADVERTISING_REPORT("Report.runGeneralAdvertiserReport", ACCOUNT_ID),
    RUN_PUBLISHER_REPORT("Report.Publisher.run", ID),
    RUN_PUBLISHER_REPORT0("Report.Publisher.run", NOT_REQUIRED),
    RUN_REFERRER_REPORT("Report.ReferrerReport.run", ID),
    RUN_REFERRER_REPORT0("Report.ReferrerReport.run", NOT_REQUIRED),
    CREATE_USER("User.create", ACCOUNT_ID),
    VIEW_USER("User.view", USER_ID),
    UPDATE_USER("User.update", USER_ID),
    VIEW_AGENT_REPORT("AgentReport.view", NOT_REQUIRED),
    EDIT_AGENT_REPORT("AgentReport.edit", NOT_REQUIRED),
    VIEW_ADVERTISER_ENTITY("AdvertiserEntity.view", NOT_REQUIRED),
    VIEW_ADVERTISING_ACCOUNT("AdvertisingAccount.view", NOT_REQUIRED),
    VIEW_AGENCY_ADVERTISER_ACCOUNT("AgencyAdvertiserAccount.view", NOT_REQUIRED)
    ;

    private String forosName;
    private RestrictionParam param;

    Restriction(String forosName, RestrictionParam param) {
        this.forosName = forosName;
        this.param = param;
    }

    public String getForosName() {
        return forosName;
    }

    public RestrictionParam getParam() {
        return param;
    }
}
