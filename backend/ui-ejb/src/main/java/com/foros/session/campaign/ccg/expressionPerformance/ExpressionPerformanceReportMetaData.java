package com.foros.session.campaign.ccg.expressionPerformance;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.MetaDataBuilder;
import com.foros.reporting.meta.ResolvableMetaData;

public class ExpressionPerformanceReportMetaData {
    public final static DbColumn EXPRESSION = buildColumn("expressionPart", "expression", ColumnTypes.string());
    public final static DbColumn IMPRESSIONS = buildColumn("impressions", "imps", ColumnTypes.number());
    public final static DbColumn CLICKS = buildColumn("clicks", "clicks", ColumnTypes.number());
    public final static DbColumn CTR = buildColumn("CTR", "ctr", ColumnTypes.percents());

    public final static ResolvableMetaData<DbColumn> META_DATA = MetaDataBuilder.metaData("stats.expressionPerformance")
        .metricsColumns(EXPRESSION, IMPRESSIONS, CLICKS, CTR)
        .build();
}
