package com.foros.reporting.meta.olap;

import com.phorm.oix.olap.OlapIdentifier;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.reporting.meta.ResolvableMetaDataImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OlapMetaDataBuilder {

    private String id;
    private List<OlapColumn> outputColumns = Collections.emptyList();
    private List<OlapColumn> metricsColumns = Collections.emptyList();

    public OlapMetaDataBuilder(String id) {
        this.id = id;
    }

    public static OlapMetaDataBuilder metaData(String id) {
        return new OlapMetaDataBuilder("reports." + id);
    }

    public OlapMetaDataBuilder outputColumns(OlapColumn... outputColumns) {
        this.outputColumns = new ArrayList<>();
        this.outputColumns.addAll(Arrays.asList(outputColumns));
        return this;
    }

    public OlapMetaDataBuilder metricsColumns(OlapColumn... metricsColumns) {
        this.metricsColumns = new ArrayList<>();
        this.metricsColumns.addAll(Arrays.asList(metricsColumns));
        return this;
    }

    public ResolvableMetaData<OlapColumn> build() {
        return new ResolvableMetaDataImpl<>(id, metricsColumns, outputColumns);
    }

    public static <C> OlapColumn cellValue(String id, OlapIdentifier columnName, ColumnType type) {
        return OlapMetaDataBuilder.buildCellValue(id, columnName, type).build();
    }

    public static <C> OlapColumn cellValue(String id, MemberResolver resolver, ColumnType type) {
        return OlapMetaDataBuilder.buildCellValue(id, resolver, type).build();
    }

    public static <C> OlapColumn cellValue(String id, MemberResolver resolver, ColumnType type, OlapColumn dependent) {
        return OlapMetaDataBuilder.buildCellValue(id, resolver, type).dependency(dependent).build();
    }

    public static OlapColumn rowMember(String id, OlapIdentifier level, ColumnType type) {
        return OlapMetaDataBuilder.buildRowMember(id, level, type).build();
    }

    public static OlapColumn rowMember(String id, OlapIdentifier level, ColumnType type, OlapColumn dependent) {
        return OlapMetaDataBuilder.buildRowMember(id, level, type)
                .dependency(dependent)
                .build();
    }

    public static OlapColumnBuilder buildRowMember(String id, OlapIdentifier column, ColumnType type) {
        return buildRowMember(id, new SimpleMemberResolver(column), type);
    }

    public static OlapColumnBuilder buildRowMember(String id, MemberResolver column, ColumnType type) {
        return column(id, OlapColumnType.OUTPUT, type, column);
    }

    public static OlapColumnBuilder buildCellValue(String id, OlapIdentifier column, ColumnType type) {
        return buildCellValue(id, new SimpleMemberResolver(column), type);
    }

    public static OlapColumnBuilder buildCellValue(String id, MemberResolver column, ColumnType type) {
        return column(id, OlapColumnType.METRIC, type, column);
    }

    private static OlapColumnBuilder column(String id, OlapColumnType olapColumnType, ColumnType type, MemberResolver column) {
        return new OlapColumnBuilder("report.output.field." + id, olapColumnType, type, column);
    }

}
