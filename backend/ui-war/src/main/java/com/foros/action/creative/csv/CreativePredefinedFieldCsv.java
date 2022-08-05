package com.foros.action.creative.csv;

import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;

import java.util.Arrays;
import java.util.List;

public class CreativePredefinedFieldCsv extends CreativeFieldCsvBase {
    public static final CreativePredefinedFieldCsv Id = new CreativePredefinedFieldCsv("Id", "ID", ColumnTypes.id(), "id");
    public static final CreativePredefinedFieldCsv Name = new CreativePredefinedFieldCsv("Name", "Name", ColumnTypes.string(), "name");
    public static final CreativePredefinedFieldCsv Status = new CreativePredefinedFieldCsv("Status", "Status", ColumnTypes.string(), "status");
    public static final CreativePredefinedFieldCsv Size = new CreativePredefinedFieldCsv("Size", "Size", ColumnTypes.string(), "size");
    public static final CreativePredefinedFieldCsv Template = new CreativePredefinedFieldCsv("Template", "Template", ColumnTypes.string(), "template");
    public static final CreativePredefinedFieldCsv VisualCategories = new CreativePredefinedFieldCsv("VisualCategories", "Visual Categories", ColumnTypes.string(), "visualCategories");
    public static final CreativePredefinedFieldCsv ContentCategories = new CreativePredefinedFieldCsv("ContentCategories", "Content Categories", ColumnTypes.string(), "contentCategories");

    // review columns
    public static final CreativePredefinedFieldCsv ValidationStatus = new CreativePredefinedFieldCsv("ValidationStatus", "Validation Status", ColumnTypes.string());
    public static final CreativePredefinedFieldCsv ErrorMessage = new CreativePredefinedFieldCsv("ErrorMessage", "Errors", ColumnTypes.string());

    public static final List<CreativePredefinedFieldCsv> PREDEFINED_COLUMNS = getPredefinedColumns();
    private static List<CreativePredefinedFieldCsv> getPredefinedColumns() {
        List<CreativePredefinedFieldCsv> result = Arrays.asList(
                Id,
                Name,
                Status,
                Size,
                Template,
                VisualCategories,
                ContentCategories
        );
        setIds(result);
        return result;
    }

    public static final List<CreativePredefinedFieldCsv> PREDEFINED_STATUS_COLUMNS = Arrays.asList(
            ValidationStatus,
            ErrorMessage
    );

    private CreativePredefinedFieldCsv(String name, String displayName, ColumnType type, String fieldPath) {
        super(name, displayName, type, fieldPath);
    }

    private CreativePredefinedFieldCsv(String name, String displayName, ColumnType type) {
        super(name, displayName, type);
    }
}
