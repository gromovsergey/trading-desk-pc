package com.foros.session.reporting.conversionPixels;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.column;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public interface ConversionPixelsMeta {
    final DbColumn DATE = buildColumn("date", "sdate", ColumnTypes.date());
    final DbColumn CONVERSION_ID = buildColumn("conversionId", "action_id", ColumnTypes.id());
    final DbColumn CONVERSION_NAME = buildColumn("conversion", "action_name", ColumnTypes.string(), CONVERSION_ID);
    final DbColumn REQUESTS = column("numberOfRequests", "requests", ColumnTypes.number()).aggregateSum().build();
    final DbColumn REQUESTS_FROM_OPTED_IN = column("fromOptedInUsers", "requests_from_opted_in", ColumnTypes.number()).aggregateSum().build();
    final DbColumn CONVERSIONS = column("conversions", "actions", ColumnTypes.number()).aggregateSum().build();
    final DbColumn REVENUE_TOTAL = column("revenueTotal", "requests_amount", ColumnTypes.currency()).aggregateSum().build();
    final DbColumn REVENUE_CONVERSIONS = column("revenueConversions", "conversions_amount", ColumnTypes.currency()).aggregateSum().build();

    public static final ResolvableMetaData<DbColumn> META_BY_DATE = metaData("conversionPixelsReport")
        .outputColumns(DATE, CONVERSION_NAME)
        .metricsColumns(REQUESTS, REQUESTS_FROM_OPTED_IN, CONVERSIONS, REVENUE_TOTAL, REVENUE_CONVERSIONS)
        .build();

    public static final ResolvableMetaData<DbColumn> META = metaData("conversionPixelsReport")
        .outputColumns(CONVERSION_NAME)
        .metricsColumns(REQUESTS, REQUESTS_FROM_OPTED_IN, CONVERSIONS, REVENUE_TOTAL, REVENUE_CONVERSIONS)
        .build();

    public static final String CONVERSION_URL_PATTERN = "../../Action/view.action?id=%d";
}
