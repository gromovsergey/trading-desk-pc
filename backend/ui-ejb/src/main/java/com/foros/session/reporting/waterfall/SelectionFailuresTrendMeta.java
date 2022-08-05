package com.foros.session.reporting.waterfall;

import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.column;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

public class SelectionFailuresTrendMeta {

    public static final DbColumn DATE = buildColumn("date", "sdate", ColumnTypes.date());
    public static final DbColumn COMBINATION = column("selectionFailuresCombination", "combination", ColumnTypes.number()).aggregateSum().build();
    public static final DbColumn COMBINATION_PC = buildColumn("selectionFailuresCombination", "combination_pc", ColumnTypes.number());
    public static final DbColumn TOTAL_BY_DATE = column("selectionFailuresTotal", "total", ColumnTypes.number()).aggregateSum().build();

    public static final ResolvableMetaData<DbColumn> META = metaData("selectionFailuresTrendReport")
            .outputColumns(DATE)
            .metricsColumns(COMBINATION, TOTAL_BY_DATE)
            .build();
}
