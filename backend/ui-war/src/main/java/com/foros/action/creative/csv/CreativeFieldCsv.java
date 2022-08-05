package com.foros.action.creative.csv;

import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;

import java.util.Collection;

public class CreativeFieldCsv extends CreativeFieldCsvBase {

    private CreativeFieldCsv(String name, String displayName, ColumnType type, String fieldPath) {
        super(name, displayName, type, fieldPath);
    }

    public static CreativeFieldCsv instantiateOption(String name) {
        return new CreativeFieldCsv(name, name, ColumnTypes.string(), name);
    }

    public static CreativeFieldCsv instantiateOptionWithType(CreativeFieldCsv src, ColumnType type) {
        if (src.getType() == type) {
            return src;
        }
        CreativeFieldCsv result = new CreativeFieldCsv(src.getName(), src.getNameKey(), type, src.getFieldPath());
        result.setId(src.getId());
        return result;
    }

    public static CreativeFieldCsv instantiatePredefined(CreativePredefinedFieldCsv src) {
        CreativeFieldCsv result = new CreativeFieldCsv(src.getName(), src.getNameKey(), src.getType(), src.getFieldPath());
        result.id = src.getId();
        return result;
    }

    public static void setOptionIds(Collection<CreativeFieldCsv> fields) {
        setIds(fields, CreativePredefinedFieldCsv.PREDEFINED_COLUMNS.size());
    }

    public void setId(int id) {
        this.id = id;
    }
}
