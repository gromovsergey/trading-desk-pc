package com.foros.action.admin.option;

import com.foros.framework.ReadOnly;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionGroup;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewOptionAction extends OptionActionSupport {

    private Long id;

    @ReadOnly
    public String view() {
        option = optionService.view(id);
        OptionGroup optionGroup = option.getOptionGroup();
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            setCreativeSizeId(optionGroup.getCreativeSize().getId());
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            setTemplateId(optionGroup.getTemplate().getId());
        }
        populateFileTypes();
        return SUCCESS;
    }

    public String getRecursiveTokensStr() {
        StringBuilder sbRecursiveToken = new StringBuilder();
        boolean isSeparatorRequired = false;
        if (option.isGenericTokensFlag()) {
            sbRecursiveToken.append(StringUtil.getLocalizedString("Option.genericTokensFlag"));
            isSeparatorRequired = true;
        }
        if (option.isAdvertiserTokensFlag()) {
            if (isSeparatorRequired) {
                sbRecursiveToken.append(", ");
            }
            sbRecursiveToken.append(StringUtil.getLocalizedString("Option.advertiserTokensFlag"));
            isSeparatorRequired = true;
        }
        if (option.isPublisherTokensFlag()) {
            if (isSeparatorRequired) {
                sbRecursiveToken.append(", ");
            }
            sbRecursiveToken.append(StringUtil.getLocalizedString("Option.publisherTokensFlag"));
            isSeparatorRequired = true;
        }
        if (option.isInternalTokensFlag()) {
            if (isSeparatorRequired) {
                sbRecursiveToken.append(", ");
            }
            sbRecursiveToken.append(StringUtil.getLocalizedString("Option.internalTokensFlag"));
        }

        return sbRecursiveToken.toString();
    }

    public List<OptionEnumValue> getDisplayValues() {
        if (option.getValues() == null || option.getValues().isEmpty()) {
            return Collections.emptyList();
        }

        List<OptionEnumValue> valueList = new ArrayList<OptionEnumValue>(option.getValues());
        Comparator<OptionEnumValue> comparator = new Comparator<OptionEnumValue>() {
            @Override
            public int compare(OptionEnumValue o1, OptionEnumValue o2) {
                return StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
            }
        };
        Collections.sort(valueList, comparator);
        return valueList;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
