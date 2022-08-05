package com.foros.action.creative.csv;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;
import com.foros.model.ExtensionProperty;
import com.foros.model.Status;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeOptionValuePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.Template;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.CurrentUserService;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.creative.CreativeCategoryComparator;
import com.foros.session.creative.CreativeCsvReaderResult;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.SizeTemplateBasedValueResolver;
import com.foros.session.creative.ValueType;
import com.foros.session.template.TemplateService;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.util.bulk.BulkReader;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.interpolator.MessageInterpolator;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class CreativeCsvReader {
    public static final ExtensionProperty<Object[]> ORIGINAL_VALUES = new ExtensionProperty<>(Object[].class);

    private final List<CreativeFieldCsv> columns = new ArrayList<>();
    private final Set<CreativeFieldCsv> optionsColumns = new LinkedHashSet<>();
    private BulkReader reader;
    private List<Creative> creatives;
    private UploadContext currentStatus;
    private BulkReader.BulkReaderRow currentRow;
    private Map<String, CreativeSize> sizesByDefaultName;
    private Map<String, CreativeTemplate> templatesByDefaultName;
    private SizeTemplateBasedValueResolver optionTypesResolver = new SizeTemplateBasedValueResolver();
    private boolean isExternal;


    public CreativeCsvReader(BulkReader reader, CreativeSizeService sizeService, TemplateService templateService, CurrentUserService currentUserService) {
        this.reader = reader;
        this.sizesByDefaultName = fetchSizes(sizeService);
        this.templatesByDefaultName = fetchTemplates(templateService);
        this.isExternal = currentUserService.isExternal();
    }

    public CreativeCsvReaderResult parse() throws IOException {
        creatives = new LinkedList<>();
        Locale locale = CurrentUserSettingsHolder.getLocale();
        final MessageInterpolator interpolator = new StringUtilsMessageInterpolator(locale);

        reader.setBulkReaderHandler(new BulkReader.BulkReaderHandler() {
            @Override
            public void handleRow(BulkReader.BulkReaderRow row) {
                currentRow = row;

                long line = row.getRowNum();
                if (line == 1) {
                    readHeader();
                    return;
                }
                currentStatus = new UploadContext();

                if (columns.size() != row.getColumnCount()) {
                    String allowed = String.valueOf(columns.size());
                    String actual = String.valueOf(row.getColumnCount());
                    currentStatus.addFatal("errors.invalid.rowFormat").withParameters(allowed, actual);
                }

                Creative creative = readCreative();
                UploadUtils.setRowNumber(creative, line);

                currentStatus.flush(interpolator);
                if (currentStatus.getStatus() == UploadStatus.REJECTED) {
                    setOriginalRecords(creative);
                }
                creative.setProperty(UPLOAD_CONTEXT, currentStatus);
                creatives.add(creative);
            }
        });

        reader.read();

        return new CreativeCsvReaderResult(creatives, getOptionColumnsNames(), getColumnTypesResolver());
    }

    private Creative readCreative() {
        Creative creative = new Creative();
        creative.setSize(new CreativeSize());
        creative.setTemplate(new CreativeTemplate());
        if (currentStatus.isFatal()) {
            return creative;
        }

        creative.setId(readLong(CreativePredefinedFieldCsv.Id));
        creative.setName(readString(CreativePredefinedFieldCsv.Name));
        setStatus(creative);
        creative.setSize(readSize());
        creative.setTemplate(readTemplate());
        creative.setCategories(readCategories());
        creative.setOptions(readOptions(creative));

        return creative;
    }

    private Long readLong(CreativePredefinedFieldCsv column) {
        String csvValue = readString(column);
        if (csvValue == null) {
            return null;
        }

        try {
            return Long.valueOf(csvValue);
        } catch (NumberFormatException e) {
            currentStatus
                    .addError("errors.field.invalid")
                    .withPath(column.getFieldPath());
        }

        return null;
    }

    private String readString(CreativePredefinedFieldCsv column) {
        return readString(column.getId());
    }

    private String readString(int index) {
        String originalString = currentRow.getStringValue(index);
        String str = index != -1 ? StringUtil.trimProperty(originalString) : null;
        return "".equals(str) ? null : str;
    }

    private CreativeSize readSize() {
        CreativeSize result = new CreativeSize();
        String defaultName = readString(CreativePredefinedFieldCsv.Size);
        result.setDefaultName(defaultName);
        CreativeSize existingSize = sizesByDefaultName.get(defaultName);
        if (existingSize == null) {
            currentStatus
                    .addFatal("creative.upload.error.invalidSize")
                    .withPath("size");
        } else {
            result.setId(existingSize.getId());
        }
        return result;
    }

    private CreativeTemplate readTemplate() {
        CreativeTemplate result = new CreativeTemplate();
        String defaultName = readString(CreativePredefinedFieldCsv.Template);
        result.setDefaultName(defaultName);
        CreativeTemplate existingTemplate = templatesByDefaultName.get(defaultName);
        if (existingTemplate == null) {
            currentStatus
                    .addFatal("creative.upload.error.invalidTemplate")
                    .withPath("template");
        } else {
            result.setId(existingTemplate.getId());
        }
        return result;
    }

    private Status readStatus(boolean isAddOperation) {
        String csvValue = readString(CreativePredefinedFieldCsv.Status);
        if (csvValue == null) {
            if (isAddOperation) {
                return Status.INACTIVE;
            }
        } else {
            try {
                return Status.valueOf(csvValue.trim().toUpperCase());
            } catch (Exception e) { }
        }

        currentStatus
                .addError("errors.field.invalid")
                .withPath(CreativePredefinedFieldCsv.Status.getFieldPath());
        return null;
    }

    private Set<CreativeCategory> readCategories() {
        Set<CreativeCategory> result = new HashSet<>();
        result.addAll(readCategories(CreativePredefinedFieldCsv.VisualCategories, CreativeCategoryType.VISUAL));
        result.addAll(readCategories(CreativePredefinedFieldCsv.ContentCategories, CreativeCategoryType.CONTENT));
        return result;
    }

    private Set<CreativeCategory> readCategories(CreativePredefinedFieldCsv field, CreativeCategoryType type) {
        String csvValue = readString(field);
        if (csvValue == null) {
            return Collections.emptySet();
        }
        String[] categories = csvValue.split(";");
        Set<CreativeCategory> result = new TreeSet<>(new CreativeCategoryComparator());
        for(String category : categories) {
            CreativeCategory resultCategory = new CreativeCategory();
            resultCategory.setDefaultName(category.trim());
            resultCategory.setType(type);

            if (!result.add(resultCategory)) {
                currentStatus
                        .addError("errors.field.invalid")
                        .withPath(field.getFieldPath())
                        .withParameters(category);
            }
        }

        return result;
    }

    private Set<CreativeOptionValue> readOptions(Creative creative) {
        CreativeSize size = sizesByDefaultName.get(creative.getSize().getDefaultName());
        CreativeTemplate template = templatesByDefaultName.get(creative.getTemplate().getDefaultName());
        if (size == null || template == null) {
            return Collections.emptySet();
        }

        Set<String> requiredOptionNames = getRequiredOptionNames(creative, size, template);
        Set<CreativeOptionValue> result = new HashSet<>();
        for (CreativeFieldCsv column : optionsColumns) {
            if (!requiredOptionNames.remove(column.getName())) {
                if (readString(column.getId()) != null) {
                    currentStatus
                            .addError("creative.upload.error.invalidOption")
                            .withPath(column.getFieldPath());
                }
                continue;
            }

            // Equal values are filtered earlier (option default names must be unique)
            result.add(readCreativeOptionValue(column, creative, size, template));
        }

        if (!requiredOptionNames.isEmpty()) {
            currentStatus
                .addFatal("creative.upload.error.missedCreativeOption")
                .withParameters(CollectionUtils.join(requiredOptionNames, "; "));
        }

        return result;
    }

    private CreativeOptionValue readCreativeOptionValue(CreativeFieldCsv column, Creative creative, CreativeSize size, CreativeTemplate template) {
        Option option = new Option();
        option.setDefaultName(column.getName());
        CreativeOptionValue optionValue = new CreativeOptionValue();
        optionValue.setCreative(creative);
        optionValue.setOption(option);

        ValueType optionType = fetchOptionType(size, template, column.getName());
        if (optionType.getValue() != null) {
            optionValue.setValue(getOptionStringValue(column, optionType.getType()));

            Option existingOption = (Option)optionType.getValue();
            option.setId(existingOption.getId());
            option.setType(existingOption.getType());
            optionValue.setId(new CreativeOptionValuePK(creative.getId() == null ? 0 : creative.getId(), existingOption.getId()));
        } else {
            currentStatus
                    .addFatal("creative.upload.error.invalidOption")
                    .withPath(column.getFieldPath());
        }

        return optionValue;
    }

    private String getOptionStringValue(CreativeFieldCsv column, ColumnType type) {
        if (type == ColumnTypes.number()) {
            try {
                Number numericValue = getOptionNumberValue(column);
                return numericValue == null ? null : String.valueOf(numericValue);
            } catch (ParseException e) {
                currentStatus
                        .addError("errors.field.number")
                        .withPath(column.getFieldPath());
            }
        }

        return currentRow.getStringValue(column.getId());
    }

    private Number getOptionNumberValue(CreativeFieldCsv column) throws ParseException {
        BigDecimal numericValue = currentRow.getNumericValue(column.getId());
        try {
            return numericValue == null ? null : numericValue.longValueExact();
        } catch (ArithmeticException e) {
            return numericValue;
        }
    }

    private Set<String> getRequiredOptionNames(Creative creative, CreativeSize size, CreativeTemplate template) {
        Collection<Option> requiredOptions = size.getAdvertiserOptions();
        requiredOptions.addAll(template.getAdvertiserOptions());

        Set<String> requiredOptionNames = new HashSet<>(requiredOptions.size());
        Set<String> duplicateOptionNames = new HashSet<>(requiredOptions.size());
        for (Option option: requiredOptions) {
            if (isExternal && option.isInternalUse()) {
                continue;
            }
            if (!requiredOptionNames.add(option.getDefaultName())) {
                duplicateOptionNames.add(option.getDefaultName());
            }
        }
        if (!duplicateOptionNames.isEmpty()) {
            currentStatus
                    .addFatal("creative.upload.error.duplicateCreativeOption")
                    .withParameters(creative.getName(), creative.getId(), CollectionUtils.join(duplicateOptionNames, "; "));
        }

        return requiredOptionNames;
    }

    private List<String> getOptionColumnsNames() {
        List<String> result = new ArrayList<>(optionsColumns.size());
        for (CreativeFieldCsv column : optionsColumns) {
            result.add(column.getName());
        }
        return result;
    }

    private void setStatus(Creative creative) {
        Status status = readStatus(creative.getId() == null);
        if (status != null) {
            creative.setStatus(status);
        }
    }

    private ValueType fetchOptionType(CreativeSize size, CreativeTemplate template, String optionDefaultName) {
        ValueType optionValueType = optionTypesResolver.getValue(optionDefaultName, size, template, null);
        if (optionValueType.getValue() == null) {
            Collection<Option> options = size.getAdvertiserOptions();
            options.addAll(template.getAdvertiserOptions());
            for (Option option : options) {
                ValueType valueType = new ValueType(option, fetchOptionType(option));
                optionTypesResolver.addValue(size, template, option.getDefaultName(), valueType);
                if (optionValueType.getValue() == null && option.getDefaultName().equals(optionDefaultName)) {
                    optionValueType = valueType;
                }
            }
        }

        return optionValueType;
    }

    private ColumnType fetchOptionType(Option option) {
        switch (option.getType()) {
            case INTEGER:
                return ColumnTypes.number();
            default:
                return ColumnTypes.string();
        }
    }

    private void setOriginalRecords(Creative creative) {
        Object[] originalRecords = new Object[columns.size()];
        for (CreativeFieldCsv column : columns) {
            if (currentRow.getColumnCount() > column.getId()) {
                originalRecords[column.getId()] = currentRow.getValue(column.getId());
            }
        }
        creative.setProperty(ORIGINAL_VALUES, originalRecords);
    }

    private void readHeader() {
        List<CreativePredefinedFieldCsv> predefinedColumns = CreativePredefinedFieldCsv.PREDEFINED_COLUMNS;
        int csvColumnsCount = currentRow.getColumnCount();
        if (predefinedColumns.size() > csvColumnsCount) {
            throw ConstraintViolationException.newBuilder("errors.invalid.header").build();
        }

        int i = 0;
        // Predefined columns
        for(; i < predefinedColumns.size(); i++) {
            CreativePredefinedFieldCsv expectedField = predefinedColumns.get(i);
            String actualName = readString(i);
            String expectedName = expectedField.getNameKey();
            if (!expectedName.equals(actualName)) {
                throw ConstraintViolationException.newBuilder("errors.invalid.header").build();
            }

            columns.add(CreativeFieldCsv.instantiatePredefined(expectedField));
        }

        // Option columns
        String firstStatusColumnName = CreativePredefinedFieldCsv.PREDEFINED_STATUS_COLUMNS.get(0).getNameKey();
        for(; i < csvColumnsCount; i++) {
            String optionName = readString(i);
            boolean isReviewColumnSection = csvColumnsCount == i + CreativePredefinedFieldCsv.PREDEFINED_STATUS_COLUMNS.size();
            if (isReviewColumnSection && firstStatusColumnName.equals(optionName)) {
                break;
            }

            CreativeFieldCsv optionColumn = CreativeFieldCsv.instantiateOption(optionName);
            if (!optionsColumns.add(optionColumn)) {
                throw ConstraintViolationException
                        .newBuilder("creative.upload.error.duplicateCsvOption")
                        .withParameters(optionName)
                        .build();
            }
        }
        columns.addAll(optionsColumns);

        // Status columns
        if (i < csvColumnsCount) {
            for (CreativePredefinedFieldCsv statusColumn : CreativePredefinedFieldCsv.PREDEFINED_STATUS_COLUMNS) {
                if (i >= csvColumnsCount) {
                    throw ConstraintViolationException.newBuilder("errors.invalid.header").build();
                }

                String expectedName = statusColumn.getNameKey();
                String reviewColumnName = readString(i++);
                if (!expectedName.equals(reviewColumnName)) {
                    throw ConstraintViolationException.newBuilder("errors.invalid.header").build();
                }

                columns.add(CreativeFieldCsv.instantiatePredefined(statusColumn));
            }
        }

        CreativeFieldCsv.setIds(columns);
    }

    private Map<String, CreativeSize> fetchSizes(CreativeSizeService sizeService) {
        Collection<CreativeSize> sizes = sizeService.findAll();
        Map<String, CreativeSize> result = new HashMap<>(sizes.size());
        for (CreativeSize size : sizes) {
            if (result.put(size.getDefaultName(), size) != null) {
                throw new RuntimeException("Duplicate size default name: " + size.getDefaultName());
            }
        }
        return result;
    }

    private Map<String, CreativeTemplate> fetchTemplates(TemplateService templateService) {
        Collection<Template> templates = templateService.findAll();
        Map<String, CreativeTemplate> result = new HashMap<>(templates.size());
        for (Template template : templates) {
            if (template instanceof CreativeTemplate && result.put(template.getDefaultName(), (CreativeTemplate)template) != null) {
                throw new RuntimeException("Duplicate template default name: " + template.getDefaultName());
            }
        }
        return result;
    }

    private SizeTemplateBasedValueResolver getColumnTypesResolver() {
        for (ValueType optionType : optionTypesResolver.getValueTypes()) {
            optionType.clearValue();
        }
        return optionTypesResolver;
    }
}
