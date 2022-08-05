package com.foros.action.admin.creativeCategories;

import com.foros.model.creative.CreativeCategory;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.util.csv.PathableCsvField;

public class CreativeCategoryFieldCsv implements PathableCsvField {


    private int id;
    private String name;

    CreativeCategoryFieldCsv(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Class<CreativeCategory> getBeanType() {
        return CreativeCategory.class;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getFieldPath() {
        return getName();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getNameKey() {
        return "channel.csv.column." + getName();
    }

    @Override
    public ColumnType getType() {
        return ColumnTypes.string();
    }

}
