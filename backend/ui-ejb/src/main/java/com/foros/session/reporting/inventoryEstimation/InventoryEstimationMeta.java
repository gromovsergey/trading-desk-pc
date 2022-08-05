package com.foros.session.reporting.inventoryEstimation;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface InventoryEstimationMeta {

    public static final DbColumn TOTAL_REQUESTS = buildColumn("totalRequests", "sum_requests", ColumnTypes.number());
    public static final DbColumn REQUESTS_EXCLUDING_PREMIUM = buildColumn(
        "requestExcludingPremium", "req_excl_prem", ColumnTypes.number());
    public static final DbColumn CMP_THRESHOLD = buildColumn("cpmThreshold", "cpm", ColumnTypes.currency());
    public static final DbColumn IMPRESSIONS = buildColumn("impressions", "imps", ColumnTypes.number());
    public static final DbColumn PASSBACKS_PC = buildColumn("passbacksPc", "passbacks_pc", ColumnTypes.percents());
    public static final DbColumn PASSBACKS = buildColumn("passbacks", "passbacks", ColumnTypes.number(), PASSBACKS_PC);
    public static final DbColumn REVENUE = buildColumn("revenue", "revenue", ColumnTypes.number());

    public static final ResolvableMetaData<DbColumn> META_DATA = metaData("inventoryEstimationReport")
        .outputColumns(CMP_THRESHOLD)
        .metricsColumns(IMPRESSIONS, PASSBACKS, REVENUE)
        .build();

    public static final ResolvableMetaData<DbColumn> SUMMARY_META = metaData("inventoryEstimationReportSummary")
        .outputColumns(TOTAL_REQUESTS)
        .metricsColumns(REQUESTS_EXCLUDING_PREMIUM)
        .build();
}
