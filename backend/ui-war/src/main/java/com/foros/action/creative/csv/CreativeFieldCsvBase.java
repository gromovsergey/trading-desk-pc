package com.foros.action.creative.csv;

import com.foros.model.creative.Creative;
import com.foros.reporting.meta.ColumnType;
import com.foros.util.csv.PathableCsvField;

import java.util.Collection;

public class CreativeFieldCsvBase implements PathableCsvField  {

    private String name;
    private String displayName;
    private ColumnType type;
    private String fieldPath;
    protected int id = -1;

    protected CreativeFieldCsvBase(String name, String displayName, ColumnType type, String fieldPath) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.fieldPath = fieldPath;
    }

    protected CreativeFieldCsvBase(String name, String displayName, ColumnType type) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
    }

    public static void setIds(Collection<? extends CreativeFieldCsvBase> fields) {
        setIds(fields, 0);
    }

    public String getName() {
        return name;
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public Class getBeanType() {
        return Creative.class;
    }

    @Override
    public String getFieldPath() {
        return fieldPath;
    }

    @Override
    public String getNameKey() {
        return displayName;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof CreativeFieldCsvBase)) {
            return false;
        }
        return name.equals(((CreativeFieldCsvBase)other).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    protected static void setIds(Collection<? extends CreativeFieldCsvBase> fields, int start) {
        int i = start;
        for (CreativeFieldCsvBase field : fields) {
            field.id = i++;
        }
    }
}
