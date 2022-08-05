package com.foros.action.creative.csv;

import com.foros.action.bulk.BulkMetaData;
import com.foros.model.creative.Creative;
import com.foros.session.creative.SizeTemplateBasedValueResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreativeCsvMetaData {
    private List<CreativeFieldCsv> columns;
    private List<CreativeFieldCsv> reviewColumns;
    private List<CreativeFieldCsv> optionColumns;
    private SizeTemplateBasedValueResolver defaultOptionValueResolver;
    private Map<Creative, Set<String>> duplicateOptionNames;

    CreativeCsvMetaData(List<CreativeFieldCsv> columns, List<CreativeFieldCsv> optionColumns,
                        SizeTemplateBasedValueResolver defaultOptionValueResolver, Map<Creative, Set<String>> duplicateOptionNames) {
        this.columns = columns;
        this.optionColumns = optionColumns;
        this.defaultOptionValueResolver = defaultOptionValueResolver;
        this.duplicateOptionNames = duplicateOptionNames;
    }

    public List<CreativeFieldCsv> getColumns() {
        return columns;
    }

    public List<CreativeFieldCsv> getReviewColumns() {
        if (reviewColumns == null) {
            List<CreativeFieldCsv> result = new ArrayList<>(columns.size() + CreativePredefinedFieldCsv.PREDEFINED_STATUS_COLUMNS.size());
            result.addAll(columns);
            for (CreativePredefinedFieldCsv predefinedField : CreativePredefinedFieldCsv.PREDEFINED_STATUS_COLUMNS) {
                result.add(CreativeFieldCsv.instantiatePredefined(predefinedField));
            }
            CreativeFieldCsv.setIds(result);
            reviewColumns = result;
        }
        return reviewColumns;
    }

    public List<CreativeFieldCsv> getOptionColumns() {
        return optionColumns;
    }

    public SizeTemplateBasedValueResolver getDefaultOptionValueResolver() {
        return defaultOptionValueResolver;
    }

    public Map<Creative, Set<String>> getDuplicateOptionNames() {
        return duplicateOptionNames;
    }

    public BulkMetaData<CreativeFieldCsv> getBulkMetaData() {
        return getBulkMetaData(getColumns());
    }

    public BulkMetaData<CreativeFieldCsv> getReviewBulkMetaData() {
        return getBulkMetaData(getReviewColumns());
    }

    private BulkMetaData<CreativeFieldCsv> getBulkMetaData(List<CreativeFieldCsv> src) {
        CreativeFieldCsv[] resultArray = new CreativeFieldCsv[src.size()];
        return new BulkMetaData<>(src.toArray(resultArray));
    }
}
