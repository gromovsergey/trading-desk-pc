package com.foros.validation.code;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BusinessErrors implements ForosError {

    // General
    GENERAL_ERROR              (200000, "Business error category"),

    // Errors that applicable for all categories
    ANY_TYPE_ERROR             (201000, "Category for errors suitable for wide range of types"),
    FIELD_IS_REQUIRED          (201001),
    FIELD_CAN_NOT_BE_CHANGED   (201002),
    FIELD_MUST_BE_NULL         (201003),

    // String errors
    STRING_ERROR               (202000, "String errors category"),
    STRING_MAX_LENGTH_CHARS    (202001),
    STRING_MAX_LENGTH_BYTES    (202002),
    STRING_INVALID_CHARS       (202003),

    // Number
    NUMBER_ERROR               (203000, "Number errors category"),
    NUMBER_OUT_OF_RANGE        (203001),
    NUMBER_EXCEED_MAX_FRACTION (203002),

    // Date & Time
    DATE_TIME_ERROR            (204000, "Date/Time errors category"),
    DATE_TIME_EARLIER          (204001),

    // URL
    URL_ERROR                  (205000, "Url errors category"),
    URL_INVALID                (205001),
    URL_INVALID_PORT           (205002),
    URL_INVALID_USERINFO       (205003),
    URL_INVALID_HOST           (205004),
    URL_INVALID_SCHEMA         (205005),

    // E-Mail
    EMAIL_ERROR                (206000, "Email errors category"),
    EMAIL_INVALID              (206001),

    // Access errors
    ACCESS_ERROR               (207000, "Access errors category"),
    ACCESS_FORBIDDEN           (207001),
    NOT_ENTITY_OWNER           (207002),
    NOT_ENTITY_MANAGER         (207003),
    OPERATION_NOT_PERMITTED    (207004),

    // File
    FILE_ERROR                 (208000, "File errors category"),
    FILE_NOT_EXIST             (208001),
    FILE_BAD_NAME              (208002),

    // Entity
    ENTITY_ERROR               (300000, "Entity errors category"),

    // Errors for various entities
    ANY_ENTITY_ERROR                                    (301000, "Entity errors category suitable for wide range of entities"),
    ENTITY_NOT_FOUND                                    (301001),
    ENTITY_DELETED                                      (301002),
    ENTITY_VERSION_COLLISION                            (301003),
    ENTITY_CHANNEL_ACCESS                               (301004),
    ENTITY_DUPLICATE                                    (301005),

    // Campaign
    CAMPAIGN_ERROR                                      (302000, "Campaigns errors category"),
    CAMPAIGN_PERIOD_HALFHOUR                            (302001, "Delivery Schedule Section", "/display/TDOC/Delivery+Schedule+Section"),
    CAMPAIGN_DYNAMIC_DELIVERY_PACING_IS_NOT_ALLOWED     (302002, "Campaign Edit Screen", "/display/TDOC/Campaign+Edit+Screen"),
    CAMPAIGN_DELIVERY_PACING_DYNAMIC_NO_END_DATE        (302003, "Campaign Edit Screen", "/display/TDOC/Campaign+Edit+Screen"),
    CAMPAIGN_DELIVERY_SCHEDULE_TIME_INTERSECTION        (302004, "Delivery Schedule Section", "/display/TDOC/Delivery+Schedule+Section"),
    CAMPAIGN_NOT_SAME_ACCOUNT_USER                      (302005, "Only users from the same account are allowed"),
    CAMPAIGN_WALLEDGARDEN_REQUIRED                      (302006, "Campaign Edit Screen", "/display/TDOC/Campaign+Edit+Screen"),

    // CCG
    CCG_ERROR                                           (303000, "CCG (Text Ad Group) errors category"),
    CCG_DELIVERY_SCHEDULE_EMPTY                         (303001, "Delivery Schedule Section", "/display/TDOC/Delivery+Schedule+Section"),
    CCG_DELIVERY_SCHEDULE_FLAG_REQUIRED                 (303002, "Delivery Schedule Section", "/display/TDOC/Delivery+Schedule+Section"),
    CCG_DELIVERY_SCHEDULE_CONFLICTED                    (303003, "Delivery Schedule Section", "/display/TDOC/Delivery+Schedule+Section"),
    CCG_TARGET_TYPE_INVALID                             (303004, "Invalid targeting type"),
    CCG_TARGET_CHANNEL_INCOMPATIBLE                     (303005, "Channel Target and Channel are incompatible"),
    CCG_RATE_TYPE_NOT_ALLOWED                           (303006),
    CCG_WALLED_GARDEN_NOT_ALLOWED                       (303007),
    CCG_DELIVERY_PACING_NO_END_DATE                     (303008),
    CCG_OPTIN_STATUS_TARGETING_INVALID                  (303009),
    CCG_FREQUENCY_CAPS_CHANGE_NOT_ALLOWED               (303010, "External user is not allowed to set FCs when Rate Type is CPA or CPC"),

    // Creative
    CREATIVE_ERROR                                      (304000),
    CREATIVE_INVALID_OPTION_ERROR                       (304001),

    // Keyword
    KEYWORD_ERROR                                       (305000, "Keyword errors category"),
    KEYWORD_GROUP_TGT_INVALID                           (305001, "Only keyword targeted groups can have keywords"),
    KEYWORD_NEGATIVE_WITH_CPC_OR_URL                    (305002, "Negative keyword can;t have URL or CPC Bid"),
    KEYWORD_NEGATIVE_CAN_NOT_BE_EMPTY                   (305003, "Empty negative keyword is not allowed"),
    KEYWORD_INVALID                                     (305004, "Keyword validation", "/display/TDOC/Channel+Matching#ChannelMatching-Keywordvalidation"),
    KEYWORD_INVALID_TYPE                                (305005),

    // Channel
    CHANNEL_ERROR                                       (307000, "Channel errors category"),
    CHANNEL_INVALID_STATUS                              (307001),
    CHANNEL_INVALID_VISIBILITY                          (307002),
    CHANNEL_RATE_TYPE_NOT_ALLOWED                       (307003),
    CHANNEL_SUPERSEDED_BY_ERROR                         (307004,"Channel Edit Screen", "/display/TDOC/Behavioral+Channel+Edit+Screen"),
    CHANNEL_CATEGORY_DELETED                            (307005),
    CHANNEL_INVALID_URL                                 (307006, "Matching URLs", "/display/TDOC/Channel+Matching#ChannelMatching-MatchingURLs"),
    CHANNEL_BP_DUPLICATE_TRIGGER_TYPE                   (307007),
    CHANNEL_BP_INVALID_TIME_INTERVAL                    (307008),
    CHANNEL_WRONG_EXPRESSION                            (307009),
    CHANNEL_EXPRESSION_SELF_LINK_LOOP                   (307010),
    CHANNEL_EXPRESSION_NOT_FOUND                        (307011),
    CHANNEL_EXPRESSION_CYCLE                            (307012),
    CHANNEL_EXPRESSION_TOO_LARGE                        (307013),
    CHANNEL_INVALID_LANGUAGE                            (307014),

    // WD Channel
    WD_CHANNEL_ERROR                                    (308000, "WD errors category"),
    WD_CHANNEL_CANT_BE_LINKED                           (308001),

    // Campaign Allocation
    CAMPAIGN_ALLOCATION_ERROR                           (309000),
    CAMPAIGN_ALLOCATION_DUPLICATE_ORDER                 (309001),
    CAMPAIGN_ALLOCATION_ILLEGAL_DELETE                  (309002),
    CAMPAIGN_ALLOCATION_MAX_SIZE_EXCEEDED               (309003),
    CAMPAIGN_ALLOCATION_INVALID_OPPORTUNITY             (309004),

    // Campaign Credit
    CAMPAIGN_CREDIT_ERROR                               (310000),
    CAMPAIGN_CREDIT_INVALID_ADVERTISER                  (310001),
    CAMPAIGN_CREDIT_ILLEGAL_DELETE                      (310002),

    // Campaign Credit Allocation
    CAMPAIGN_CREDIT_ALLOCATION_ERROR                    (311000),
    CAMPAIGN_CREDIT_ALLOCATION_CAMPAIGN_SAME_ADVERTISER (311001),

    // Regular Checkable
    REGULAR_CHECKABLE_ERROR                             (312000),
    REGULAR_CHECKABLE_INTERVAL_INVALID                  (312001),

    // Site Creative Approval
    SITE_CREATIVE_APPROVAL_ERROR                        (313000),
    SITE_CREATIVE_APPROVAL_CAN_NOT_APPROVE              (313001),

    // Operations
    OPERATION_ERROR                                     (400000),

    REPORT_ERROR                                        (401000),
    REPORT_WRONG_COLUMN                                 (401001),

    // Advertising reports
    ADVERTISING_REPORT_ERROR                       (402000),
    ADV_REPORT_COST_AND_RATES_INTERNAL_ONLY        (402001),
    ADV_REPORT_INVALID_ACCOUNT                     (402002, "Direct advertiser or Agency account is expected"),
    ADV_REPORT_ADVERTISER_MUST_NOT_BE_STANDALONE   (402003, "In-Agency Advertiser is expected"),
    ADV_REPORT_ENTITY_FROM_DIFFERENT_ACCOUNT       (402004, "All entities must belong to one account"),
    ADV_REPORT_INVALID_CAMPAIGN                    (402005),
    ADV_REPORT_INVALID_GROUP                       (402006),
    ADV_REPORT_INVALID_CREATIVE                    (402007)
    ;

    static final Map<ForosError, List<String>> MAP_OF_ERRORS = Collections.unmodifiableMap(new HashMap<ForosError, List<String>>(){
        {
            put(FIELD_IS_REQUIRED, "errors.field.required", "errors.required");
            put(FIELD_CAN_NOT_BE_CHANGED,
                    "errors.canNotChange",
                    "errors.field.canNotChange",
                    "CreativeTemplate.size.linked.update",
                    "CreativeTemplate.applicationFormat.linked.update"
            );
            put(FIELD_MUST_BE_NULL, "errors.field.null");
            put(STRING_MAX_LENGTH_CHARS, "errors.field.maxlength");
            put(STRING_MAX_LENGTH_BYTES, "errors.field.invalidMaxLengthExc");
            put(STRING_INVALID_CHARS,"errors.field.illegalSymbols");
            put(NUMBER_OUT_OF_RANGE,
                    "errors.greater",
                    "errors.field.less",
                    "errors.field.notgreater",
                    "errors.field.range"
            );
            put(NUMBER_EXCEED_MAX_FRACTION,
                    "errors.maxFractionDigits",
                    "errors.field.maxFractionDigits");
            put(DATE_TIME_EARLIER, "errors.dates");
            put(URL_INVALID, "errors.url", "errors.field.invalidUrl");
            put(URL_INVALID_PORT, "errors.url.port", "errors.url.httpPortOnly");
            put(URL_INVALID_USERINFO, "errors.url.userinfo");
            put(URL_INVALID_HOST, "errors.url.emptyHost", "errors.url.host");
            put(URL_INVALID_SCHEMA, "errors.url.schema", "errors.url.schema.canBeEmpty");
            put(FILE_NOT_EXIST, "errors.fileexist");
            put(FILE_BAD_NAME, "errors.invalidfile");
            put(EMAIL_INVALID, "errors.email");

            put(ACCESS_FORBIDDEN, "errors.forbidden", "error.operation.not.permitted");
            put(NOT_ENTITY_OWNER, "errors.not.entity.owner");
            put(NOT_ENTITY_MANAGER, "errors.not.entity.manager");
            put(OPERATION_NOT_PERMITTED, "errors.operation.not.permitted");

            // Entity
            put(ENTITY_NOT_FOUND, "errors.entity.notFound");
            put(ENTITY_DELETED, "errors.entity.deleted");
            put(ENTITY_VERSION_COLLISION, "errors.version");
            put(ENTITY_CHANNEL_ACCESS,
                    "errors.expression.wrongType",
                    "errors.expression.wrongVisibility.internal",
                    "errors.expression.wrongVisibility.advertiser",
                    "errors.expression.wrongVisibility.agency",
                    "errors.expression.wrongVisibility.cmp",
                    "errors.expression.deleted",
                    "errors.expression.inactive",
                    "errors.expression.livePendingInactivation",
                    "errors.expression.notLive",
                    "errors.expression.wrongCountry",
                    "errors.channel.deleted",
                    "errors.channel.inactive",
                    "errors.channel.livePendingInactivation",
                    "errors.channel.wrongVisibility.internal",
                    "errors.channel.wrongVisibility.advertiser",
                    "errors.channel.wrongVisibility.agency",
                    "errors.channel.wrongVisibility.cmp",
                    "errors.channel.wrongType"
            );
            put(ENTITY_DUPLICATE,
                    "errors.duplicate",
                    "errors.keyword.duplicate",
                    "errors.duplicate.name",
                    "errors.duplicate.id"
            );

            //Campaign
            put(CAMPAIGN_PERIOD_HALFHOUR, "campaign.errors.period.halfhour.timeFrom", "campaign.errors.period.halfhour.timeTo");
            put(CAMPAIGN_WALLEDGARDEN_REQUIRED, "WalledGarden.validation.marketplace");
            put(CAMPAIGN_DYNAMIC_DELIVERY_PACING_IS_NOT_ALLOWED, "errors.dynamicDeliveryPacingIsNotAllowed");
            put(CAMPAIGN_DELIVERY_PACING_DYNAMIC_NO_END_DATE); // used in code
            put(CAMPAIGN_DELIVERY_SCHEDULE_TIME_INTERSECTION, "errors.intersection");
            put(CAMPAIGN_NOT_SAME_ACCOUNT_USER, "campaign.errors.soldToUser", "campaign.errors.billToUser");

            //CCG
            put(CCG_DELIVERY_SCHEDULE_EMPTY, "deliverySchedule.campaignCreativeGroup.errors");
            put(CCG_DELIVERY_SCHEDULE_FLAG_REQUIRED, "deliverySchedule.deliveryFlag.required");
            put(CCG_DELIVERY_SCHEDULE_CONFLICTED, "errors.deliverySchedule.conflicted");
            put(CCG_TARGET_TYPE_INVALID, "ccg.error.target.type");
            put(CCG_TARGET_CHANNEL_INCOMPATIBLE, "ccg.error.target");
            put(CCG_RATE_TYPE_NOT_ALLOWED, "errors.rateTypeIsNotAllowed");
            put(CCG_WALLED_GARDEN_NOT_ALLOWED, "errors.textGroupsNotAllowedWalledGarden", "errors.walledGardenNotAllowedRateType");
            put(CCG_DELIVERY_PACING_NO_END_DATE, "ccg.deliveryPacing.dynamic.noEndDate");
            put(CCG_OPTIN_STATUS_TARGETING_INVALID, "ccg.error.optInStatusTargeting");
            put(CCG_FREQUENCY_CAPS_CHANGE_NOT_ALLOWED, "ccg.error.rate.type.fcNotAllowed");

            //Creative
            put(CREATIVE_ERROR,
                    "creative.wrongType.error",
                    "creative.wrongTemplate.error",
                    "creative.wrongExpansion.expandableSize",
                    "creative.wrongExpansion.size",
                    "creative.wrongExpansion.template"
            );

            put(CREATIVE_INVALID_OPTION_ERROR,
                    "creative.option.duplicate",
                    "creative.option.notFound",
                    "creative.option.unresolved",
                    "creative.option.token.notFound",
                    "creative.option.token.multiple"
            );

            //Keyword
            put(KEYWORD_GROUP_TGT_INVALID, "ccg.keyword.onlyKeywordTargetedCCG");
            put(KEYWORD_NEGATIVE_WITH_CPC_OR_URL, "errors.negative.keywords.CPCorURL");
            put(KEYWORD_NEGATIVE_CAN_NOT_BE_EMPTY, "errors.emptyNegativeKeyword");
            put(KEYWORD_INVALID, "errors.invalidKeyword", "errors.keyword.squareBracketsIncorrectPlace", "errors.keyword.squareBracketsNotAllowed");
            put(KEYWORD_INVALID_TYPE, "errors.keyword.invalidType");

            // Channel
            put(CHANNEL_INVALID_STATUS, "channel.errors.invalidStatus");
            put(CHANNEL_INVALID_VISIBILITY, "channel.errors.invalidVisibility");
            put(CHANNEL_RATE_TYPE_NOT_ALLOWED, "channel.errors.rateTypeIsNotAllowed");
            put(CHANNEL_SUPERSEDED_BY_ERROR,
                    "channel.supersededByChannel.wrongAccount",
                    "channel.supersededByChannel.wrongCountry",
                    "channel.supersededByChannel.self",
                    "channel.supersededByChannel.new",
                    "channel.supersededByChannel.advertising"
            );
            put(CHANNEL_CATEGORY_DELETED, "channel.errors.invalidCategory");
            put(CHANNEL_INVALID_URL, "errors.invalidurl");
            put(CHANNEL_BP_DUPLICATE_TRIGGER_TYPE, "errors.behavioralParameters.duplicateTriggerType");
            put(CHANNEL_BP_INVALID_TIME_INTERVAL, "errors.behavioralParameters.timeInterval.invalid");
            put(CHANNEL_WRONG_EXPRESSION, "errors.wrong.cdml");
            put(CHANNEL_EXPRESSION_SELF_LINK_LOOP, "errors.expression.self.link.loop");
            put(CHANNEL_EXPRESSION_NOT_FOUND, "errors.channelNotFound");
            put(CHANNEL_EXPRESSION_CYCLE, "errors.expression.cycle");
            put(CHANNEL_EXPRESSION_TOO_LARGE, "errors.expression.tooLarge");
            put(CHANNEL_INVALID_LANGUAGE, "channel.errors.language");
            put(WD_CHANNEL_CANT_BE_LINKED, "DiscoverChannel.errors.notLinked");

            // Campaign Allocation
            put(CAMPAIGN_ALLOCATION_DUPLICATE_ORDER, "campaignAllocation.errors.duplicateOrder");
            put(CAMPAIGN_ALLOCATION_ILLEGAL_DELETE, "campaignAllocation.errors.illegalDelete");
            put(CAMPAIGN_ALLOCATION_MAX_SIZE_EXCEEDED, "campaignAllocation.errors.maxSize");
            put(CAMPAIGN_ALLOCATION_INVALID_OPPORTUNITY, "campaignAllocation.errors.opportunity.invalid");

            // Campaign Credit
            put(CAMPAIGN_CREDIT_INVALID_ADVERTISER, "CampaignCredit.errors.advertiser");
            put(CAMPAIGN_CREDIT_ILLEGAL_DELETE, "CampaignCredit.errors.illegalDelete");

            // Campaign Credit Allocation
            put(CAMPAIGN_CREDIT_ALLOCATION_CAMPAIGN_SAME_ADVERTISER, "CampaignCreditAllocation.errors.campaign.sameAdvertiser");

            // Regular Checkable
            put(REGULAR_CHECKABLE_INTERVAL_INVALID, "checks.errors.interval");

            // Site Creative Approval
            put(SITE_CREATIVE_APPROVAL_CAN_NOT_APPROVE, "site.creativesApproval.error.noApproveOrReject");

            // Report
            put(REPORT_WRONG_COLUMN, "errors.advertiserReport.column.wrong");

            // Text Advertising report
            put(ADV_REPORT_COST_AND_RATES_INTERNAL_ONLY, "errors.advertiserReport.costAndRates");
            put(ADV_REPORT_INVALID_ACCOUNT, "errors.advertiserReport.advertiser.invalid.role");
            put(ADV_REPORT_ADVERTISER_MUST_NOT_BE_STANDALONE, "errors.advertiserReport.advertiser.standalone");
            put(ADV_REPORT_ENTITY_FROM_DIFFERENT_ACCOUNT, "errors.advertiserReport.filters.belongAccount");
            put(ADV_REPORT_INVALID_CAMPAIGN, "errors.advertiserReport.campaign.text", "errors.advertiserReport.campaign.display");
            put(ADV_REPORT_INVALID_GROUP, "errors.advertiserReport.group.text", "errors.advertiserReport.group.display");
            put(ADV_REPORT_INVALID_CREATIVE, "errors.advertiserReport.creative.text", "errors.advertiserReport.creative.display");

        }

        void put(ForosError error, String... key) {
            put(error, Collections.unmodifiableList(Arrays.asList(key)));
        }
    });

    private int code;
    private String text;
    private String url;

    BusinessErrors(int code) {
        this(code, null, null);
    }

    BusinessErrors(int code, String text) {
        this(code, text, null);
    }

    BusinessErrors(int code, String text, String url) {
        this.code = code;
        this.text = text;
        this.url = url;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getText() {
        return text;
    }
}
