package com.foros.action.campaign.bulk;

import com.foros.model.account.Account;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.Creative;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.MetaDataImpl;
import com.foros.security.principal.SecurityContext;
import com.foros.session.ServiceLocator;
import com.foros.session.campaign.AdvertiserEntityRestrictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetaDataBuilder {

    private static final List<Column> ALL_KEYWORD_TARGETED_COLUMNS = new ArrayList<Column>(Arrays.asList(
            CampaignFieldCsv.Level,
            CampaignFieldCsv.CampaignName,
            CampaignFieldCsv.CampaignBudget,
            CampaignFieldCsv.CampaignStatus,
            CampaignFieldCsv.CampaignStartDate,
            CampaignFieldCsv.CampaignEndDate,
            CampaignFieldCsv.CampaignSalesManager,
            CampaignFieldCsv.CampaignSoldToUser,
            CampaignFieldCsv.CampaignBillToUser,
            CampaignFieldCsv.CampaignDailyBudget,
            CampaignFieldCsv.CampaignFCPeriod,
            CampaignFieldCsv.CampaignFCWindow,
            CampaignFieldCsv.CampaignFCWindowLength,
            CampaignFieldCsv.CampaignFCLife,
            CampaignFieldCsv.AdGroupName,
            CampaignFieldCsv.AdGroupRate,
            CampaignFieldCsv.AdGroupRateType,
            CampaignFieldCsv.AdGroupStatus,
            CampaignFieldCsv.AdGroupBudget,
            CampaignFieldCsv.AdGroupDailyBudget,
            CampaignFieldCsv.AdGroupStartDate,
            CampaignFieldCsv.AdGroupEndDate,
            CampaignFieldCsv.AdGroupCountryTargeting,
            CampaignFieldCsv.AdGroupDeviceTargeting,
            CampaignFieldCsv.AdGroupFCPeriod,
            CampaignFieldCsv.AdGroupFCWindow,
            CampaignFieldCsv.AdGroupFCWindowLength,
            CampaignFieldCsv.AdGroupFCLife,
            CampaignFieldCsv.AdLinkId,
            CampaignFieldCsv.AdId,
            CampaignFieldCsv.AdHeadline,
            CampaignFieldCsv.AdDescriptionLine1,
            CampaignFieldCsv.AdDescriptionLine2,
            CampaignFieldCsv.AdDescriptionLine3,
            CampaignFieldCsv.AdDescriptionLine4,
            CampaignFieldCsv.AdDisplayURL,
            CampaignFieldCsv.AdClickURL,
            CampaignFieldCsv.AdImageFile,
            CampaignFieldCsv.AdLinkStatus,
            CampaignFieldCsv.AdStatus,
            CampaignFieldCsv.AdApproval,
            CampaignFieldCsv.AdFCPeriod,
            CampaignFieldCsv.AdFCWindow,
            CampaignFieldCsv.AdFCWindowLength,
            CampaignFieldCsv.AdFCLife,
            CampaignFieldCsv.Keyword,
            CampaignFieldCsv.KeywordType,
            CampaignFieldCsv.KeywordRate,
            CampaignFieldCsv.KeywordClickURL,
            CampaignFieldCsv.KeywordStatus
    ));

    private static final List<Column> ALL_CHANNEL_TARGETED_COLUMNS = new ArrayList<Column>(Arrays.asList(
            CampaignFieldCsv.Level,
            CampaignFieldCsv.CampaignName,
            CampaignFieldCsv.CampaignBudget,
            CampaignFieldCsv.CampaignStatus,
            CampaignFieldCsv.CampaignStartDate,
            CampaignFieldCsv.CampaignEndDate,
            CampaignFieldCsv.CampaignSalesManager,
            CampaignFieldCsv.CampaignSoldToUser,
            CampaignFieldCsv.CampaignBillToUser,
            CampaignFieldCsv.CampaignDailyBudget,
            CampaignFieldCsv.CampaignFCPeriod,
            CampaignFieldCsv.CampaignFCWindow,
            CampaignFieldCsv.CampaignFCWindowLength,
            CampaignFieldCsv.CampaignFCLife,
            CampaignFieldCsv.AdGroupName,
            CampaignFieldCsv.AdGroupRate,
            CampaignFieldCsv.AdGroupRateType,
            CampaignFieldCsv.AdGroupStatus,
            CampaignFieldCsv.AdGroupBudget,
            CampaignFieldCsv.AdGroupDailyBudget,
            CampaignFieldCsv.AdGroupStartDate,
            CampaignFieldCsv.AdGroupEndDate,
            CampaignFieldCsv.AdGroupCountryTargeting,
            CampaignFieldCsv.AdGroupChannelTarget,
            CampaignFieldCsv.AdGroupDeviceTargeting,
            CampaignFieldCsv.AdGroupFCPeriod,
            CampaignFieldCsv.AdGroupFCWindow,
            CampaignFieldCsv.AdGroupFCWindowLength,
            CampaignFieldCsv.AdGroupFCLife,
            CampaignFieldCsv.AdLinkId,
            CampaignFieldCsv.AdId,
            CampaignFieldCsv.AdHeadline,
            CampaignFieldCsv.AdDescriptionLine1,
            CampaignFieldCsv.AdDescriptionLine2,
            CampaignFieldCsv.AdDescriptionLine3,
            CampaignFieldCsv.AdDescriptionLine4,
            CampaignFieldCsv.AdDisplayURL,
            CampaignFieldCsv.AdClickURL,
            CampaignFieldCsv.AdImageFile,
            CampaignFieldCsv.AdLinkStatus,
            CampaignFieldCsv.AdStatus,
            CampaignFieldCsv.AdApproval,
            CampaignFieldCsv.AdFCPeriod,
            CampaignFieldCsv.AdFCWindow,
            CampaignFieldCsv.AdFCWindowLength,
            CampaignFieldCsv.AdFCLife
    ));

    private static final Set<Column> INTERNAL_COLUMNS = new HashSet<Column>(Arrays.asList(
            CampaignFieldCsv.CampaignSalesManager
    ));

    private static final Set<Column> APPROVAL_COLUMNS = new HashSet<Column>(Arrays.asList(
            CampaignFieldCsv.AdApproval
    ));

    public static final Set<Column> ALL_CAMPAIGN_COLUMNS = columnsForClass(Campaign.class);
    public static final Set<Column> ALL_AD_GROUP_COLUMNS
            = columnsForClass(CampaignCreativeGroup.class, CampaignFieldCsv.CampaignName);
    public static final Set<Column> ALL_KEYWORD_COLUMNS
            = columnsForClass(CCGKeyword.class, CampaignFieldCsv.CampaignName, CampaignFieldCsv.AdGroupName);
    public static final Set<Column> ALL_CREATIVES_COLUMNS
            = columnsForClass(Creative.class, CampaignCreative.class, CampaignFieldCsv.CampaignName, CampaignFieldCsv.AdGroupName);




    private MetaData<Column> allColumns;

    public MetaDataBuilder(Account account, TGTType tgtType) {
        List<Column> allFields;

        if (TGTType.KEYWORD == tgtType) {
            allFields = new ArrayList<>(ALL_KEYWORD_TARGETED_COLUMNS);
        } else {
            allFields = new ArrayList<>(ALL_CHANNEL_TARGETED_COLUMNS);
        }

        if (account.getAccountType().isInvoicingFlag()) {
            allFields.remove(CampaignFieldCsv.CampaignBillToUser);
        }

        if (!SecurityContext.isInternal()) {
            allFields.removeAll(INTERNAL_COLUMNS);
        }

        AdvertiserEntityRestrictions restrictions = ServiceLocator.getInstance().lookup(AdvertiserEntityRestrictions.class);

        if (!restrictions.canApprove()) {
            allFields.removeAll(APPROVAL_COLUMNS);
        }

        allColumns = new MetaDataImpl<>(allFields);
    }

    public MetaData<Column> forUpload() {
        return allColumns;
    }

    public MetaData<Column> forExport() {
        return allColumns.include(
                CampaignFieldCsv.Impressions,
                CampaignFieldCsv.Clicks,
                CampaignFieldCsv.CTR,
                CampaignFieldCsv.Cost
        );
    }

    public MetaData<Column> forReview() {
        return allColumns.include(
                CampaignFieldCsv.ValidationStatus,
                CampaignFieldCsv.Errors
        );
    }

    private static Set<Column> columnsForClass(Class clazz1, Class clazz2,  CampaignFieldCsv... extraColumn) {
        Set<Column> set = new HashSet<>();
        set.addAll(columnsForClass(clazz1, extraColumn));
        set.addAll(columnsForClass(clazz2));
        return Collections.unmodifiableSet(set);
    }

    private static Set<Column> columnsForClass(Class clazz, CampaignFieldCsv... extraColumn) {
        Set<Column> res = new HashSet<>();
        for (CampaignFieldCsv column : CampaignFieldCsv.values()) {
            Class columnClass = column.getBeanType();
            if (columnClass == null || columnClass.isAssignableFrom(clazz)) {
                res.add(column);
            }
        }
        res.addAll(Arrays.asList(extraColumn));
        return Collections.unmodifiableSet(res);
    }
}
