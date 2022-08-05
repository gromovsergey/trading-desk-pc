package com.foros.action.campaign.bulk;

import com.foros.model.EntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.model.creative.TextCreativeOption;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.util.csv.PathableCsvField;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum CampaignFieldCsv implements PathableCsvField {
    // entity columns
    Level(ColumnTypes.string(), EntityBase.class, "level"),
    CampaignName(ColumnTypes.string(), Campaign.class, "name"),
    CampaignBudget(ColumnTypes.currency(), Campaign.class, "budget"),
    CampaignStatus(ColumnTypes.status(), Campaign.class, "status"),
    CampaignStartDate(ColumnTypes.dateTime(), Campaign.class, "dateStart"),
    CampaignEndDate(ColumnTypes.dateTime(), Campaign.class, "dateEnd"),
    CampaignSalesManager(ColumnTypes.string(), Campaign.class, "salesManager"),
    CampaignSoldToUser(ColumnTypes.string(), Campaign.class, "soldToUser"),
    CampaignBillToUser(ColumnTypes.string(), Campaign.class, "billToUser"),
    CampaignDailyBudget(ColumnTypes.currency(), Campaign.class, "dailyBudget"),
    CampaignFCPeriod(ColumnTypes.timeSpan(), Campaign.class, "frequencyCap.periodSpan"),
    CampaignFCWindow(ColumnTypes.number(), Campaign.class, "frequencyCap.windowCount"),
    CampaignFCWindowLength(ColumnTypes.timeSpan(), Campaign.class, "frequencyCap.windowLengthSpan"),
    CampaignFCLife(ColumnTypes.number(), Campaign.class, "frequencyCap.lifeCount"),
    AdGroupName(ColumnTypes.string(), CampaignCreativeGroup.class, "name"),
    AdGroupRate(ColumnTypes.currency(), CampaignCreativeGroup.class, "ccgRate"),
    AdGroupRateType(ColumnTypes.string(), CampaignCreativeGroup.class, "rateType"),
    AdGroupStatus(ColumnTypes.status(), CampaignCreativeGroup.class, "status"),
    AdGroupBudget(ColumnTypes.currency(), CampaignCreativeGroup.class, "budget"),
    AdGroupDailyBudget(ColumnTypes.currency(), CampaignCreativeGroup.class, "dailyBudget"),
    AdGroupStartDate(ColumnTypes.dateTime(), CampaignCreativeGroup.class, "dateStart"),
    AdGroupEndDate(ColumnTypes.dateTime(), CampaignCreativeGroup.class, "dateEnd"),
    AdGroupCountryTargeting(ColumnTypes.string(), CampaignCreativeGroup.class, "country"),
    AdGroupDeviceTargeting(ColumnTypes.string(), CampaignCreativeGroup.class, "deviceChannels"),
    AdGroupChannelTarget(ColumnTypes.string(), CampaignCreativeGroup.class, "channelTarget"),
    AdGroupFCPeriod(ColumnTypes.timeSpan(), CampaignCreativeGroup.class, "frequencyCap.periodSpan"),
    AdGroupFCWindow(ColumnTypes.number(), CampaignCreativeGroup.class, "frequencyCap.windowCount"),
    AdGroupFCWindowLength(ColumnTypes.timeSpan(), CampaignCreativeGroup.class, "frequencyCap.windowLengthSpan"),
    AdGroupFCLife(ColumnTypes.number(), CampaignCreativeGroup.class, "frequencyCap.lifeCount"),
    AdLinkId(ColumnTypes.id(), CampaignCreative.class, "id"),
    AdId(ColumnTypes.id(), Creative.class, "creativeId"),
    AdHeadline(ColumnTypes.string(), Creative.class, "headline", TextCreativeOption.HEADLINE),
    AdDescriptionLine1(ColumnTypes.string(), Creative.class, "descriptionLine1", TextCreativeOption.DESCRIPTION_LINE_1),
    AdDescriptionLine2(ColumnTypes.string(), Creative.class, "descriptionLine2", TextCreativeOption.DESCRIPTION_LINE_2),
    AdDescriptionLine3(ColumnTypes.string(), Creative.class, "descriptionLine3", TextCreativeOption.DESCRIPTION_LINE_3),
    AdDescriptionLine4(ColumnTypes.string(), Creative.class, "descriptionLine4", TextCreativeOption.DESCRIPTION_LINE_4),
    AdDisplayURL(ColumnTypes.string(), Creative.class, "displayUrl", TextCreativeOption.DISPLAY_URL),
    AdClickURL(ColumnTypes.string(), Creative.class, "clickUrl", TextCreativeOption.CLICK_URL),
    AdImageFile(ColumnTypes.string(), Creative.class, "imageFile", TextCreativeOption.IMAGE_FILE),
    AdLinkStatus(ColumnTypes.status(), CampaignCreative.class, "campaignCreative.status"),
    AdStatus(ColumnTypes.status(), Creative.class, "status"),
    AdApproval(ColumnTypes.qaStatus(), Creative.class, "qaStatus"),
    AdFCPeriod(ColumnTypes.timeSpan(), CampaignCreative.class, "frequencyCap.periodSpan"),
    AdFCWindow(ColumnTypes.number(), CampaignCreative.class, "frequencyCap.windowCount"),
    AdFCWindowLength(ColumnTypes.timeSpan(), CampaignCreative.class, "frequencyCap.windowLengthSpan"),
    AdFCLife(ColumnTypes.number(), CampaignCreative.class, "frequencyCap.lifeCount"),
    Keyword(ColumnTypes.string(), CCGKeyword.class, "originalKeyword"),
    KeywordType(ColumnTypes.string(), CCGKeyword.class, "triggerType"),
    KeywordRate(ColumnTypes.currency(), CCGKeyword.class, "maxCpcBid"),
    KeywordClickURL(ColumnTypes.string(), CCGKeyword.class, "clickURL"),
    KeywordStatus(ColumnTypes.status(), CCGKeyword.class, "status"),
    // stats columns
    Impressions(ColumnTypes.number()),
    Clicks(ColumnTypes.number()),
    CTR(ColumnTypes.percents()),
    Cost(ColumnTypes.currency()),
    // review columns
    ValidationStatus(ColumnTypes.string()),
    Errors(ColumnTypes.string());

    public static final int TOTAL_COLUMNS_COUNT = CampaignFieldCsv.values().length;

    public static final Map<String, CampaignFieldCsv> TEXT_OPTIONS = Collections.unmodifiableMap(new HashMap<String, CampaignFieldCsv>() {
        {
            put(CampaignFieldCsv.AdHeadline.getTextOption().getToken(), CampaignFieldCsv.AdHeadline);
            put(CampaignFieldCsv.AdDescriptionLine1.getTextOption().getToken(), CampaignFieldCsv.AdDescriptionLine1);
            put(CampaignFieldCsv.AdDescriptionLine2.getTextOption().getToken(), CampaignFieldCsv.AdDescriptionLine2);
            put(CampaignFieldCsv.AdDescriptionLine3.getTextOption().getToken(), CampaignFieldCsv.AdDescriptionLine3);
            put(CampaignFieldCsv.AdDescriptionLine4.getTextOption().getToken(), CampaignFieldCsv.AdDescriptionLine4);
            put(CampaignFieldCsv.AdDisplayURL.getTextOption().getToken(), CampaignFieldCsv.AdDisplayURL);
            put(CampaignFieldCsv.AdClickURL.getTextOption().getToken(), CampaignFieldCsv.AdClickURL);
            put(CampaignFieldCsv.AdImageFile.getTextOption().getToken(), CampaignFieldCsv.AdImageFile);
        }
    });

    private ColumnType type;
    private Class beanType;
    private String fieldPath;
    private TextCreativeOption textOption;

    CampaignFieldCsv(ColumnType type) {
        this.type = type;
    }

    CampaignFieldCsv(ColumnType type, Class beanType, String fieldPath) {
        this(type, beanType, fieldPath, null);
    }

    CampaignFieldCsv(ColumnType type, Class beanType, String fieldPath, TextCreativeOption option) {
        this.type = type;
        this.beanType = beanType;
        this.fieldPath = fieldPath;
        this.textOption = option;
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public Class getBeanType() {
        return beanType;
    }

    @Override
    public String getFieldPath() {
        return fieldPath;
    }

    @Override
    public String getNameKey() {
        return "campaign.csv.column." + name();
    }

    public TextCreativeOption getTextOption() {
        return textOption;
    }
}
