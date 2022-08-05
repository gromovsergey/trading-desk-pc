package com.foros.action.creative.csv;

import com.foros.model.creative.Creative;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.session.creative.SizeTemplateBasedValueResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetaDataBuilder {

    public static CreativeCsvMetaData buildMetaData(Collection<Creative> creatives, boolean isExternal) {
        OptionsInfo optionsInfo = getOptionsInfoFromCreatives(creatives, isExternal);
        List<CreativeFieldCsv> creativeFields = getExportColumnsList(optionsInfo.getOptionFields());

        return instantiateCreativeCsvMetaData(creativeFields, optionsInfo);
    }

    public static CreativeCsvMetaData buildReviewMetaData(List<String> optionHeaderNames, SizeTemplateBasedValueResolver columnTypeResolver) {
        OptionsInfo optionsInfo = getOptionsInfoFromOptionNames(optionHeaderNames, columnTypeResolver);
        List<CreativeFieldCsv> creativeFields = getExportColumnsList(optionsInfo.getOptionFields());

        return instantiateCreativeCsvMetaData(creativeFields, optionsInfo);
    }

    private static List<CreativeFieldCsv> getExportColumnsList(List<CreativeFieldCsv> optionsFields) {
        int size = CreativePredefinedFieldCsv.PREDEFINED_COLUMNS.size() +
                optionsFields.size() + CreativePredefinedFieldCsv.PREDEFINED_STATUS_COLUMNS.size();
        List<CreativeFieldCsv> result = new ArrayList<>(size);

        for (CreativePredefinedFieldCsv predefinedField : CreativePredefinedFieldCsv.PREDEFINED_COLUMNS) {
            result.add(CreativeFieldCsv.instantiatePredefined(predefinedField));
        }
        result.addAll(optionsFields);
        CreativeFieldCsv.setIds(result);

        return result;
    }

    private static OptionsInfo getOptionsInfoFromCreatives(Collection<Creative> creatives, boolean isExternal) {
        Set<String> sizeOptionNames = new LinkedHashSet<>();
        Set<String> templateOptionNames = new LinkedHashSet<>();
        SizeTemplateBasedValueResolver defaultOptionValueResolver = new SizeTemplateBasedValueResolver();
        Map<Creative, Set<String>> duplicateOptionNames = new LinkedHashMap<>();

        for (Creative creative : creatives) {
            DuplicateOptionValueChecker duplicateChecker = new DuplicateOptionValueChecker();

            createDefaultValueResolversOptions(creative, creative.getSize().getOptionGroups(), sizeOptionNames, duplicateChecker, defaultOptionValueResolver, isExternal);
            createDefaultValueResolversOptions(creative, creative.getTemplate().getOptionGroups(), templateOptionNames, duplicateChecker, defaultOptionValueResolver, isExternal);

            if (!duplicateChecker.getDuplicateOptionDefaultNames().isEmpty()) {
                duplicateOptionNames.put(creative, duplicateChecker.getDuplicateOptionDefaultNames());
            }
        }

        sizeOptionNames.addAll(templateOptionNames);
        List<CreativeFieldCsv> fields = generateCsvFields(sizeOptionNames);

        return new OptionsInfo(fields, duplicateOptionNames, defaultOptionValueResolver);
    }

    private static OptionsInfo getOptionsInfoFromOptionNames(List<String> optionHeaderNames, SizeTemplateBasedValueResolver columnTypeResolver) {
        List<CreativeFieldCsv> optionFields = new ArrayList<>(optionHeaderNames.size());
        for (String optionHeaderName : optionHeaderNames) {
            optionFields.add(CreativeFieldCsv.instantiateOption(optionHeaderName));
        }

        CreativeFieldCsv.setOptionIds(optionFields);

        return new OptionsInfo(optionFields, columnTypeResolver);
    }

    private static void createDefaultValueResolversOptions(Creative creative, Set<OptionGroup> groups, Set<String> optionNames,
            DuplicateOptionValueChecker duplicateChecker, SizeTemplateBasedValueResolver defaultOptionValueResolver, boolean isExternal) {
        for (OptionGroup group : groups) {
            if (group.getType() == OptionGroupType.Advertiser) {
                for (Option option : group.getOptions()) {
                    if (option.isInternalUse() && isExternal) {
                        continue;
                    }

                    if (duplicateChecker.isDuplicate(option)) {
                        continue;
                    }

                    optionNames.add(option.getDefaultName());
                    defaultOptionValueResolver.addValue(creative.getSize(), creative.getTemplate(), option.getDefaultName(), option.getDefaultValue(), getType(option));
                }
            }
        }
    }

    private static List<CreativeFieldCsv> generateCsvFields(Set<String> optionNames) {
        List<CreativeFieldCsv> result = new ArrayList<>(optionNames.size());
        for (String optionName : optionNames) {
            result.add(CreativeFieldCsv.instantiateOption(optionName));
        }
        CreativeFieldCsv.setOptionIds(result);
        return result;
    }

    private static ColumnType getType(Option option) {
        switch (option.getType()) {
            case INTEGER:
                return ColumnTypes.number();
            default:
                return ColumnTypes.string();
        }
    }

    private static CreativeCsvMetaData instantiateCreativeCsvMetaData(List<CreativeFieldCsv> creativeFields, OptionsInfo optionsInfo) {
        return new CreativeCsvMetaData(creativeFields,
                                       optionsInfo.getOptionFields(),
                                       optionsInfo.getDefaultOptionValueResolver(),
                                       optionsInfo.getDuplicateOptionNames());
    }

    private static class DuplicateOptionValueChecker {
        private Set<String> optionDefaultNames = new HashSet<>();
        private Set<String> duplicateOptionDefaultNames = new LinkedHashSet<>();

        public boolean isDuplicate(Option option) {
            if (optionDefaultNames.add(option.getDefaultName())) {
                return false;
            }

            duplicateOptionDefaultNames.add(option.getDefaultName());
            return true;
        }

        public Set<String> getDuplicateOptionDefaultNames() {
            return duplicateOptionDefaultNames;
        }
    }

    private static class OptionsInfo {
        private List<CreativeFieldCsv> optionFields;
        private Map<Creative, Set<String>> duplicateOptionNames;
        private SizeTemplateBasedValueResolver defaultOptionValueResolver;

        OptionsInfo(List<CreativeFieldCsv> optionFields, Map<Creative, Set<String>> duplicateOptionNames, SizeTemplateBasedValueResolver defaultOptionValueResolver) {
            this.optionFields = optionFields;
            this.duplicateOptionNames = duplicateOptionNames;
            this.defaultOptionValueResolver = defaultOptionValueResolver;
        }

        OptionsInfo(List<CreativeFieldCsv> optionFields, SizeTemplateBasedValueResolver columnTypeResolver) {
            this(optionFields, Collections.<Creative, Set<String>>emptyMap(), columnTypeResolver);
        }

        public List<CreativeFieldCsv> getOptionFields() {
            return optionFields;
        }

        public Map<Creative, Set<String>> getDuplicateOptionNames() {
            return duplicateOptionNames;
        }

        public SizeTemplateBasedValueResolver getDefaultOptionValueResolver() {
            return defaultOptionValueResolver;
        }
    }
}
