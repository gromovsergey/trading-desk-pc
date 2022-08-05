package com.foros.session.template;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionFileType;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.session.BeanValidations;
import com.foros.session.UrlValidations;
import com.foros.session.fileman.FileUtils;
import com.foros.util.CollectionMerger;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.preview.PreviewHelper;
import com.foros.util.url.URLValidator;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@LocalBean
@Stateless
@Validations
public class OptionValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private ConfigService configService;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private OptionValueValidations optionValueValidations;

    @EJB
    private UrlValidations urlValidations;

    @Validation
    public void validateCreate(
            ValidationContext context,
            @ValidateBean(ValidationMode.CREATE) Option option) {
        validateSubjectToType(context, option, option.getType(), true);
    }

    @Validation
    public void validateUpdate(
            ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) Option option) {

        Option existing = em.find(Option.class, option.getId());
        if (existing == null) {
            throw new EntityNotFoundException("Option with id=" + option.getId() + " not found");
        }

        validateGroup(context, option, existing);

        if (validateType(context, option, existing)) {

            OptionType type = option.isChanged("type") ? option.getType() : existing.getType();
            boolean typeWasChanged = option.isChanged("type") && existing.getType() != option.getType();

            validateSubjectToType(context, option, type, typeWasChanged);
        }
    }

    private void validateGroup(ValidationContext context, Option option, Option existing) {

        beanValidations.linkValidator(context, OptionGroup.class)
                .withPath("optionGroup")
                .validate(option.getOptionGroup());

        if (context.hasViolation("optionGroup")) {
            return;
        }

        OptionGroup newGroup = em.find(OptionGroup.class, option.getOptionGroup().getId());
        OptionGroup existingGroup = existing.getOptionGroup();

        // group should be from the same template
        if (existingGroup.getTemplate() != null && !existingGroup.getTemplate().equals(newGroup.getTemplate())) {
            context.addConstraintViolation("errors.field.invalid")
                    .withPath("optionGroup");
        }

        // or from same size
        if (existingGroup.getCreativeSize() != null && !existingGroup.getCreativeSize().equals(newGroup.getCreativeSize())) {
            context.addConstraintViolation("errors.field.invalid")
                    .withPath("optionGroup");
        }

        // and type should be the same
        if (existingGroup.getType() != newGroup.getType()) {
            context.addConstraintViolation("errors.field.invalid")
                    .withPath("optionGroup");
        }
    }

    private boolean hasNonEmptyOptionValues(Option option) {
        String sql = " select " +
                " exists ( " +
                "   select 1 from CreativeOptionValue ov " +
                "   where  ov.value is not null and ov.option_id = :optionId " +
                " ) " +
                " or exists ( " +
                "   select 1 from TagOptionValue ov " +
                "   where  ov.value is not null and ov.option_id = :optionId " +
                " ) " +
                " or exists ( " +
                "   select 1 from WDTagOptionValue ov " +
                "   where  ov.value is not null and ov.option_id = :optionId " +
                " ) ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("optionId", option.getId());
        return (boolean) query.getSingleResult();
    }

    private boolean validateType(ValidationContext context, Option option, Option existing) {
        if (option.isChanged("type")
                && existing.getType() != option.getType()
                && !allowTypeChange(existing.getType(), option.getType())
                && hasNonEmptyOptionValues(option)) {
            context
                    .addConstraintViolation("Option.error.cannotChangeType")
                    .withPath("type")
                    .withValue(option.getType());
            return false;
        }
        return true;
    }

    private boolean allowTypeChange(OptionType existingType, OptionType newType) {
        return (newType == OptionType.STRING || newType == OptionType.TEXT)
                && (existingType == OptionType.INTEGER || existingType == OptionType.COLOR || existingType == OptionType.URL || existingType == OptionType.URL_WITHOUT_PROTOCOL || existingType == OptionType.STRING)
                || (existingType == OptionType.HTML && newType == OptionType.TEXT)
                || (existingType == OptionType.STRING && newType == OptionType.URL_WITHOUT_PROTOCOL);
    }

    private void validateSubjectToType(ValidationContext context, Option option, OptionType type, boolean typeWasChanged) {
        ValidationContext defaultValueContext = context.createSubContext(option.getDefaultValue(), "defaultValue");
        optionValueValidations.validateValueLength(defaultValueContext, option, option.getDefaultValue());
        String substituted = optionValueValidations.validateSubstitutions(defaultValueContext, option, option.getDefaultValue());
        if (defaultValueContext.hasViolations()) {
            return;
        }

        switch (type) {
            case INTEGER:
                validateIntegerOption(context, option, typeWasChanged);
                break;
            case ENUM:
                validateEnumOption(context, option, typeWasChanged);
                validateEmptyDefaultValue(context, option, typeWasChanged);
                break;
            case COLOR:
                validateColorOption(context, option, typeWasChanged);
                break;
            case FILE:
            case DYNAMIC_FILE:
                validateFileTypes(context, option, typeWasChanged);
                validateEmptyDefaultValue(context, option, typeWasChanged);
                break;
            case FILE_URL:
                validateFileTypes(context, option, typeWasChanged);
                validateUrlDefaultValue(context, option, typeWasChanged, substituted);
                break;
            case URL:
                validateUrlDefaultValue(context, option, typeWasChanged, substituted);
                break;
            case URL_WITHOUT_PROTOCOL:
                validateUrlWithoutSchemaDefaultValue(context, option, typeWasChanged, substituted);
                break;
            case HTML:
                validateEmptyDefaultValue(context, option, typeWasChanged);
            default:
                break;
        }
    }

    private void validateIntegerOption(ValidationContext context, Option option, boolean typeWasChanged) {
        Long optionId = option.getId();
        Long minValue = option.getMinValue();
        Long maxValue = option.getMaxValue();
        String strDefaultValue = option.getDefaultValue();
        Long defaultValue = null;

        if ((typeWasChanged || option.isChanged("defaultValue"))
                && StringUtil.isPropertyNotEmpty(strDefaultValue)) {
            String trimmedValue = strDefaultValue;

            if (trimmedValue.startsWith("-")) {
                trimmedValue = trimmedValue.substring(1);
            }

            if (trimmedValue.length() > 10) {
                context
                        .addConstraintViolation("errors.field.range")
                        .withParameters(minValue != null ? minValue : "-9,999,999,999", maxValue != null ? maxValue : "9,999,999,999")
                        .withPath("defaultValue");
            } else if (!StringUtil.isNumber(strDefaultValue)) {
                context
                        .addConstraintViolation("errors.field.integer")
                        .withPath("defaultValue");
            } else {
                defaultValue = StringUtil.convertToLong(strDefaultValue);
            }

            if (defaultValue != null) {
                if (minValue != null && minValue > defaultValue) {
                    context
                            .addConstraintViolation("errors.field.less")
                            .withParameters(minValue)
                            .withPath("defaultValue");
                }

                if (maxValue != null && maxValue < defaultValue) {
                    context
                            .addConstraintViolation("errors.field.notgreater")
                            .withParameters(maxValue)
                            .withPath("defaultValue");
                }
            }
        }

        if ((typeWasChanged || option.isChanged("minValue") || option.isChanged("maxValue"))
                && minValue != null && maxValue != null && minValue > maxValue) {
            context
                    .addConstraintViolation("errors.field.notgreater")
                    .withParameters(maxValue)
                    .withPath("minValue");
        }

        if ((typeWasChanged || option.isChanged("minValue"))
                && optionId != null && minValue != null && !checkIntegerMinValue(optionId, minValue)) {
            context
                    .addConstraintViolation("errors.field.notgreater")
                    .withParameters("{Option.minExistingValue}")
                    .withPath("minValue");
        }

        if ((typeWasChanged || option.isChanged("maxValue"))
                && optionId != null && maxValue != null && !checkIntegerMaxValue(optionId, maxValue)) {
            context
                    .addConstraintViolation("errors.field.less")
                    .withParameters("{Option.maxExistingValue}")
                    .withPath("maxValue");
        }
    }

    private void validateEnumOption(ValidationContext context, Option option, boolean typeWasChanged) {
        if (typeWasChanged || option.isChanged("values")) {
            Set<OptionEnumValue> values = option.getValues();

            if (CollectionUtils.isNullOrEmpty(values)) {
                context
                        .addConstraintViolation("Option.error.notEnoughEnumValues")
                        .withPath("values")
                        .withValue(values);
                return;
            } else if (values.size() < 2) {
                context
                        .addConstraintViolation("Option.error.notEnoughEnumValues")
                        .withPath("values")
                        .withValue(values);
            } else if (values.size() > 100) {
                context
                        .addConstraintViolation("Option.error.tooManyEnumValues")
                        .withParameters("100")
                        .withPath("values")
                        .withValue(values);
            }

            Set<String> valuesSet = new HashSet<String>(values.size());
            Set<String> namesSet = new HashSet<String>(values.size());
            boolean hasDefault = false;

            Iterator<OptionEnumValue> it = values.iterator();

            for (int i = 0; it.hasNext(); i++) {
                OptionEnumValue value = it.next();

                if (value != null) {
                    hasDefault |= value.isDefault();
                }

                String valueStr = value == null ? null : value.getValue();
                String nameStr = value == null ? null : value.getName();

                if (value == null || StringUtil.isPropertyEmpty(valueStr)) {
                    context
                            .addConstraintViolation("errors.field.required")
                            .withPath("values[" + i + "].value");
                }

                if (value == null || StringUtil.isPropertyEmpty(nameStr)) {
                    context
                            .addConstraintViolation("errors.field.required")
                            .withPath("values[" + i + "].name");
                }

                if (value != null) {
                    if (StringUtil.isPropertyNotEmpty(valueStr)) {
                        if (valueStr.length() > 50) {
                            context
                                    .addConstraintViolation("errors.field.maxlength")
                                    .withParameters("50")
                                    .withPath("values[" + i + "].value");
                        }

                        if (valuesSet.contains(valueStr)) {
                            context
                                    .addConstraintViolation("Option.error.duplicateValue")
                                    .withPath("values[" + i + "].value")
                                    .withValue(valueStr);
                        } else {
                            valuesSet.add(valueStr);
                        }
                    }

                    if (StringUtil.isPropertyNotEmpty(nameStr)) {
                        if (nameStr.length() > 50) {
                            context
                                    .addConstraintViolation("errors.field.maxlength")
                                    .withParameters("50")
                                    .withPath("values[" + i + "].name");
                        }

                        if (namesSet.contains(nameStr)) {
                            context
                                    .addConstraintViolation("Option.error.duplicateName")
                                    .withPath("values[" + i + "].name")
                                    .withValue(nameStr);
                        } else {
                            namesSet.add(nameStr);
                        }
                    }
                }
            }

            if (!hasDefault) {
                context
                        .addConstraintViolation("Option.error.noEnumDefaultValue")
                        .withPath("values")
                        .withValue(values);
            }

            if (option.getId() != null && !checkEnumValues(option)) {
                context
                        .addConstraintViolation("Option.error.cannotRemoveValues")
                        .withPath("values")
                        .withValue(values);
            }
        }

        // Option with enum type should be required
        if ((typeWasChanged || option.isChanged("required"))
                && !option.isRequired()) {
            context
                    .addConstraintViolation("Option.error.enumNotRequired")
                    .withPath("required")
                    .withValue(option.isRequired());
        }
    }

    private void validateColorOption(ValidationContext context, Option option, boolean typeWasChanged) {
        if (typeWasChanged || option.isChanged("defaultValue")) {
            String defaultColor = option.getDefaultValue();
            if (StringUtil.isPropertyNotEmpty(defaultColor) && !StringUtil.ONLY_COLOR_LETTERS.matcher(defaultColor).matches()) {
                context
                        .addConstraintViolation("Option.error.invalidColor")
                        .withPath("defaultValue")
                        .withValue(defaultColor);
            }
        }
    }

    private void validateFileTypes(ValidationContext context, Option option, boolean typeWasChanged) {
        if (typeWasChanged || option.isChanged("fileTypes")) {
            List<String> allowedMimeTypes = FileUtils.fileTypesToMimeTypes(configService.get(ConfigParameters.ALLOWED_FILE_TYPES));

            for (OptionFileType fileType : option.getFileTypes()) {
                if (!allowedMimeTypes.contains(FileUtils.getMimeTypeByExtension(fileType.getFileType()))) {
                    context
                            .addConstraintViolation("Option.error.invalidFileType")
                            .withParameters(fileType.getFileType())
                            .withPath("fileTypes")
                            .withValue(fileType.getFileType());
                }
            }
        }
    }

    private void validateEmptyDefaultValue(ValidationContext context, Option option, boolean typeWasChanged) {
        // Default value for file and enum types should be empty
        if ((typeWasChanged || option.isChanged("defaultValue"))
                && StringUtil.isPropertyNotEmpty(option.getDefaultValue())) {
            context
                    .addConstraintViolation("Option.error.notEmptyDefaultValue")
                    .withPath("defaultValue")
                    .withValue(option.getDefaultValue());
        }
    }

    private void validateUrlDefaultValue(ValidationContext context, Option option, boolean typeWasChanged, String value) {
        if (typeWasChanged || option.isChanged("defaultValue")) {
            // No files is allowed as default value only URLs.
            urlValidations.validateUrl(context, URLValidator.urlForValidate(value), "defaultValue", true);
        }
    }

    private void validateUrlWithoutSchemaDefaultValue(ValidationContext context, Option option, boolean typeWasChanged, String value) {
        if (typeWasChanged || option.isChanged("defaultValue")) {
            urlValidations.validateUrl(context, value, "defaultValue", UrlValidations.NO_SCHEMA, true);
        }
    }

    private boolean checkEnumValues(Option option) {
        Long optionId = option.getId();
        Set<OptionEnumValue> values = option.getValues();
        Option existing = em.find(Option.class, optionId);
        final List<String> updatedValues = new LinkedList<String>();
        // get intersection of existing values
        new CollectionMerger<OptionEnumValue>(existing.getValues(), values) {
            @Override
            protected boolean add(OptionEnumValue updated) {
                return false;
            }

            @Override
            protected void update(OptionEnumValue persistent, OptionEnumValue updated) {
                updatedValues.add(updated.getValue());
            }

            @Override
            protected boolean delete(OptionEnumValue updated) {
                return false;
            }
        }.merge();
        if (updatedValues.size() == 0) {
            return true;
        }
        Query q = em.createNativeQuery("select distinct value" +
                " from (select option_id, value from creativeoptionvalue" +
                " union all select option_id, value from wdtagoptionvalue) q " +
                " where option_id = :optionId and value not in (:enumValue)");
        q.setParameter("optionId", optionId);
        q.setParameter("enumValue", updatedValues);

        @SuppressWarnings("unchecked")
        List<String> result = q.getResultList();
        boolean existOtherValues = (result.size() > 0);
        return !existOtherValues;
    }

    private boolean checkIntegerMinValue(Long optionId, Long minValue) {
        Query q = em.createNativeQuery("select not exists(select 1 " +
                " from (select option_id, value from creativeoptionvalue" +
                " union all select option_id, value from wdtagoptionvalue) q" +
                " where option_id = :optionId and value ~ E'^\\\\d+$' and cast(value as numeric) < :minValue)");
        return (boolean) q
                .setParameter("optionId", optionId)
                .setParameter("minValue", minValue)
                .getSingleResult();
    }

    private boolean checkIntegerMaxValue(Long optionId, Long maxValue) {
        Query q = em.createNativeQuery("select not exists(select 1 " +
                " from (select option_id, value from creativeoptionvalue" +
                " union all select option_id, value from wdtagoptionvalue) q" +
                " where option_id = :optionId and value ~ E'^\\\\d+$' and cast(value as numeric) > :maxValue)");
        return (boolean) q
                .setParameter("optionId", optionId)
                .setParameter("maxValue", maxValue)
                .getSingleResult();
    }
}
