package com.foros.action.creative.csv;

import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.session.creative.SizeTemplateBasedValueResolver;
import com.foros.session.creative.ValueType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CreativeCsvNodeWriter implements CsvNodeWriter<Creative> {
    private final CreativeCsvMetaData metaData;

    public CreativeCsvNodeWriter(CreativeCsvMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public void write(CsvRow row, Creative entity) {
        row.set(CreativePredefinedFieldCsv.Id, entity.getId());
        row.set(CreativePredefinedFieldCsv.Name, entity.getName());
        row.set(CreativePredefinedFieldCsv.Status, entity.getStatus().name());
        row.set(CreativePredefinedFieldCsv.Size, entity.getSize().getDefaultName());
        row.set(CreativePredefinedFieldCsv.Template, entity.getTemplate().getDefaultName());
        row.set(CreativePredefinedFieldCsv.VisualCategories, categoriesToString(entity.getCategories(), CreativeCategoryType.VISUAL));
        row.set(CreativePredefinedFieldCsv.ContentCategories, categoriesToString(entity.getCategories(), CreativeCategoryType.CONTENT));

        setOptions(row, entity);
    }

    protected void setOptions(CsvRow row, Creative entity) {
        CreativeSize size = entity.getSize();
        CreativeTemplate template = entity.getTemplate();

        Map<String, String> optionsMap = new HashMap<>(entity.getOptions().size());
        for (CreativeOptionValue optionValue : entity.getOptions()) {
            Option option = optionValue.getOption();
            if (isOptionFromSizeOrTemplate(option, size, template)) {
                optionsMap.put(option.getDefaultName(), optionValue.getValue());
            }
        }

        SizeTemplateBasedValueResolver defaultValueResolver = metaData.getDefaultOptionValueResolver();
        for (CreativeFieldCsv field : metaData.getOptionColumns()) {
            ValueType optionValueType = defaultValueResolver.getValue(
                    field.getName(), size, template, optionsMap.get(field.getName()));

            row.set(CreativeFieldCsv.instantiateOptionWithType(field, optionValueType.getType()), castOptionValue(optionValueType));
        }
    }

    private String categoriesToString(Set<CreativeCategory> categories, CreativeCategoryType type) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (CreativeCategory category : categories) {
            if (category.getType() == type) {
                if (first) {
                    first = false;
                } else {
                    result.append(';');
                }
                result.append(category.getDefaultName());
            }
        }

        return result.toString();
    }

    private Object castOptionValue(ValueType optionValueType) {
        if (optionValueType.getValue() == null) {
            return null;
        }
        Object value = optionValueType.getValue();
        if (optionValueType.getType().equals(ColumnTypes.number())) {
            if (value instanceof String) {
                return new BigDecimal((String)value);
            }
            if (value instanceof Long) {
                return BigDecimal.valueOf((Long)value);
            }
        }
        return value;
    }

    protected boolean isOptionFromSizeOrTemplate(Option option, CreativeSize size, CreativeTemplate template) {
        return size != null && size.hasOption(option) || template != null && template.hasOption(option);
    }
}
