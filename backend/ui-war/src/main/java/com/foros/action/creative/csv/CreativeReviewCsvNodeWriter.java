package com.foros.action.creative.csv;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;
import com.foros.action.bulk.CsvRow;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.Template;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.template.OptionService;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.csv.ErrorMessageBuilder;
import com.foros.util.mapper.Converter;
import com.foros.validation.constraint.convertion.SimpleConstraintViolationConverter;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.List;
import java.util.Map;

public class CreativeReviewCsvNodeWriter extends CreativeCsvNodeWriter {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add(
                    "options[(#path)].value",
                    "#optionMessageConverter.getPath(groups[0])",
                    "#optionMessageConverter.getMessage(groups[0], violation.message)"
            )
            .rules();

    private final CreativeFieldCsv VALIDATION_STATUS;
    private final CreativeFieldCsv ERROR_MESSAGE;
    private final CreativeCsvMetaData metaData;
    private final Map<String, Option> options;

    public CreativeReviewCsvNodeWriter(CreativeCsvMetaData metaData, final OptionService optionService) {
        super(metaData);

        this.metaData = metaData;
        this.options = CollectionUtils.lazyMap(new Converter<String, Option>() {
            @Override
            public Option item(String optionId) {
                return optionService.findById(Long.valueOf(optionId));
            }
        });

        VALIDATION_STATUS = CreativeFieldCsv.instantiatePredefined(CreativePredefinedFieldCsv.ValidationStatus);
        VALIDATION_STATUS.setId(metaData.getColumns().size());

        ERROR_MESSAGE = CreativeFieldCsv.instantiatePredefined(CreativePredefinedFieldCsv.ErrorMessage);
        ERROR_MESSAGE.setId(metaData.getColumns().size() + 1);
    }

    @Override
    protected boolean isOptionFromSizeOrTemplate(Option option, CreativeSize size, CreativeTemplate template) {
        Option existingOption = options.get(option.getId().toString());
        return super.isOptionFromSizeOrTemplate(existingOption, size, template);
    }

    @Override
    public void write(CsvRow row, Creative entity) {
        boolean originalsWritten = writeOriginal(row, entity);
        if (!originalsWritten) {
            super.write(row, entity);
        }
        writeStatus(row, entity);
    }

    private boolean writeOriginal(CsvRow row, Creative entity) {
        Object[] originalValues = entity.getProperty(CreativeCsvReader.ORIGINAL_VALUES);
        if (originalValues == null) {
            return false;
        }

        for (CreativeFieldCsv column : metaData.getColumns()) {
            row.set(column, originalValues[column.getId()]);
        }
        row.setUnparsed(true);
        return true;
    }

    private void writeStatus(CsvRow row, final Creative entity) {
        UploadContext context = entity.getProperty(UPLOAD_CONTEXT);
        row.set(VALIDATION_STATUS, context.getStatus().name());

        if (context.getStatus() != UploadStatus.REJECTED) {
            row.set(ERROR_MESSAGE, null);
            return;
        }

        CreativeFieldCsv[] buffer = new CreativeFieldCsv[metaData.getColumns().size()];
        ErrorMessageBuilder builder = new ErrorMessageBuilder<>(metaData.getColumns().toArray(buffer), entity.getClass());

        SimpleConstraintViolationConverter converter = new SimpleConstraintViolationConverter(builder);
        converter.addToContext("optionMessageConverter", new Object() {
            public String getMessage(String optionId, String violationMessage) {
                Option option = options.get(optionId);

                CreativeSize size = option.getOptionGroup().getCreativeSize();
                if (size != null) {
                    if (size.getId().equals(entity.getSize().getId())) {
                        return violationMessage;
                    }
                    return StringUtil.getLocalizedString("creative.upload.error.invalidSizeOption",
                            option.getDefaultName(),
                            size.getDefaultName(),
                            violationMessage);
                }

                Template template = option.getOptionGroup().getTemplate();
                if (template != null) {
                    if (template.getId().equals(entity.getTemplate().getId())) {
                        return violationMessage;
                    }
                    return StringUtil.getLocalizedString("creative.upload.error.invalidTemplateOption",
                            option.getDefaultName(),
                            template.getDefaultName(),
                            violationMessage);
                }

                throw new RuntimeException("OptionGroup is not linked to any size or template " + optionId);
            }

            public String getPath(String optionId) {
                return options.get(optionId).getDefaultName();
            }
        });
        converter.applyRules(RULES, context.getErrors());

        row.set(ERROR_MESSAGE, builder.build());
    }
}
