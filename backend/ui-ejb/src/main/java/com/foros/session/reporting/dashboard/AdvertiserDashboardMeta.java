package com.foros.session.reporting.dashboard;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.MetaDataBuilder;
import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.reporting.tools.subtotal.aggreagate.ECPMAggregateFunction;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.column;

public interface AdvertiserDashboardMeta {
    DbColumn ACCOUNT_ID = buildColumn("accountId", "account_id", ColumnTypes.id());
    DbColumn ACCOUNT_NAME = buildColumn("accountName", "name", ColumnTypes.string(), ACCOUNT_ID);

    DbColumn CAMPAIGN_ID = buildColumn("campaignId", "campaign_id", ColumnTypes.id());
    DbColumn CAMPAIGN = buildColumn("campaign", "name", ColumnTypes.string(), CAMPAIGN_ID);

    DbColumn RANGE_UNIQUE_USERS = buildColumn("uniqueUsers", "unique_users_range", ColumnTypes.number());

    DbColumn AD_DISPLAY_STATUS = buildColumn("displayStatusId", "display_status_id", ColumnTypes.id(), ACCOUNT_ID);

    DbColumn CAMPAIGN_DISPLAY_STATUS = buildColumn("displayStatusId", "display_status_id", ColumnTypes.string(), CAMPAIGN_ID);

    DbColumn CCGS_PENDING_USER = buildColumn("ccgsPendingUser", "pending_ccgs_user", ColumnTypes.number());

    DbColumn CREATIVES_PENDING_USER = buildColumn("creativesPendingUser", "pending_creatives_user", ColumnTypes.number());

    DbColumn CREATIVES_PENDING_FOROS = buildColumn("creativesPendingForos", "pending_creatives_foros", ColumnTypes.number());

    DbColumn IMPRESSIONS = column("impressions", "imps", ColumnTypes.number()).aggregateSum().build();

    DbColumn TOTAL_COST = column("totalCost", "total_cost", ColumnTypes.currency()).aggregateSum().build();

    DbColumn ECPM = column("eCPM", "ecpm", ColumnTypes.currency()).aggregate(ECPMAggregateFunction.factory(TOTAL_COST, IMPRESSIONS)).build();

    DbColumn TARGETING_COST = column("targetingCost", "targeting_cost", ColumnTypes.currency()).aggregateSum().build();

    DbColumn CLICKS = column("clicks", "clicks", ColumnTypes.number()).aggregateSum().build();

    DbColumn CTR = column("CTR", "ctr", ColumnTypes.percents()).aggregatePercent(CLICKS, IMPRESSIONS).build();

    DbColumn INVENTORY_COST = column("inventoryCost", "inventory_cost", ColumnTypes.currency()).aggregateSum().build();

    DbColumn CAMPAIGN_CREDIT_USED = column("campaignCreditUsed", "campaign_credit_used", ColumnTypes.currency()).aggregateSum().build();

    ResolvableMetaData<DbColumn> ADVERTISER_DASHBOARD = MetaDataBuilder.metaData("AdvertiserDashboard")
            .outputColumns(ACCOUNT_ID, ACCOUNT_NAME, AD_DISPLAY_STATUS)
            .metricsColumns(
                    IMPRESSIONS,
                    CLICKS,
                    CTR,
                    RANGE_UNIQUE_USERS,
                    TARGETING_COST,
                    INVENTORY_COST,
                    TOTAL_COST,
                    CAMPAIGN_CREDIT_USED,
                    ECPM,
                    CCGS_PENDING_USER,
                    CREATIVES_PENDING_USER,
                    CREATIVES_PENDING_FOROS
                    ).build();

    ResolvableMetaData<DbColumn> CAMPAIGN_DASHBOARD = MetaDataBuilder.metaData("CampaignDashboard")
            .outputColumns(CAMPAIGN_ID, CAMPAIGN, CAMPAIGN_DISPLAY_STATUS)
            .metricsColumns(
                    IMPRESSIONS,
                    CLICKS,
                    CTR,
                    RANGE_UNIQUE_USERS,
                    TARGETING_COST,
                    INVENTORY_COST,
                    TOTAL_COST,
                    CAMPAIGN_CREDIT_USED,
                    ECPM,
                    CCGS_PENDING_USER,
                    CREATIVES_PENDING_USER,
                    CREATIVES_PENDING_FOROS
                    ).build();
}
