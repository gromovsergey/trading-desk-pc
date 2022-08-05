package com.foros.action.admin.country.placementsBlacklist;

import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.util.csv.PathableCsvField;

public enum PlacementBlacklistFieldCsv implements PathableCsvField {

    // Entity columns
    Url(ColumnTypes.string(), PlacementBlacklist.class, "url"),
    AdSize(ColumnTypes.string(), PlacementBlacklist.class, "adSize"),
    Action(ColumnTypes.string(), PlacementBlacklist.class, "action"),
    Reason(ColumnTypes.string(), PlacementBlacklist.class, "reason"),

    // Review columns
    ValidationStatus(ColumnTypes.string()),
    Errors(ColumnTypes.string());

    public static final int TOTAL_COLUMNS_COUNT = PlacementBlacklistFieldCsv.values().length;

    private ColumnType type;
    private Class beanType;
    private String fieldPath;

    PlacementBlacklistFieldCsv(ColumnType type) {
        this.type = type;
    }

    PlacementBlacklistFieldCsv(ColumnType type, Class beanType, String fieldPath) {
        this.type = type;
        this.beanType = beanType;
        this.fieldPath = fieldPath;
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
        return "admin.placementsBlacklist.csv.column." + name();
    }
}
