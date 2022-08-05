package com.foros.session.reporting.campaignOverview;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface CampaignOverviewReportMeta {
    DbColumn VERTICAL = buildColumn("vertical", "vertical", ColumnTypes.string());
    DbColumn PRODUCT = buildColumn("product", "product", ColumnTypes.string());
    DbColumn ADVERTISERS_COUNT = buildColumn("advertisersCount", "advertisers_cnt", ColumnTypes.number());
    DbColumn CAMPAIGNS_COUNT = buildColumn("campaignsCount", "campaigns_cnt", ColumnTypes.number());
    DbColumn IMPRESSIONS = buildColumn("impressions", "imps", ColumnTypes.number());
    DbColumn CLICKS = buildColumn("clicks", "clicks", ColumnTypes.number());
    DbColumn SPENT_BUDGET = buildColumn("budgetSpent", "spent_budget", ColumnTypes.currency());
    DbColumn ECPM = buildColumn("eCPM", "ecpm", ColumnTypes.currency());
    DbColumn ECPC = buildColumn("eCPC", "ecpc", ColumnTypes.currency());

    ResolvableMetaData<DbColumn> CAMPAIGN_OVERVIEW_REPORT_SUMMARY = metaData("campaignOverviewSummary")
        .outputColumns(IMPRESSIONS)
        .metricsColumns(CLICKS, SPENT_BUDGET)
        .build();

    ResolvableMetaData<DbColumn> CAMPAIGN_OVERVIEW_REPORT = metaData("campaignOverview")
        .outputColumns(VERTICAL)
        .metricsColumns(PRODUCT, ADVERTISERS_COUNT, CAMPAIGNS_COUNT, IMPRESSIONS, CLICKS, SPENT_BUDGET, ECPM, ECPC)
        .build();
}
