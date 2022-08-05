package com.foros.action.admin.option;

import com.foros.action.IdNameBean;
import com.foros.action.Invalidable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionFileType;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.session.template.OptionGroupService;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "minValue", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "maxValue", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "integerDefaultValue", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "maxLength", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "maxLengthFullWidth", key = "errors.field.integer")
        }
)
public class SaveOptionAction extends OptionActionSupport implements Invalidable {
    @EJB
    private OptionGroupService optionGroupService;

    public SaveOptionAction() {
        super();
        option = new Option();
    }

    public String create() {
        prepareSave();
        if (hasFieldErrors()) {
            return INPUT;
        }

        optionService.create(option);
        return SUCCESS;
    }

    public String update() {
        prepareSave();
        if (hasFieldErrors()) {
            return INPUT;
        }

        optionService.update(option);
        return SUCCESS;
    }

    private void prepareSave() {
        if (StringUtil.isPropertyEmpty(option.getDefaultValue())) {
            option.setDefaultValue(null);
        }

        if (StringUtil.isPropertyEmpty(option.getDefaultLabel())) {
            option.setDefaultLabel(null);
        }

        switch (option.getType()) {
            case INTEGER:
                String defaultValue = integerDefaultValue != null ? integerDefaultValue.toString() : null;
                option.setDefaultValue(defaultValue);
                break;
            case ENUM:
                prepareValues();
                option.setRequired(true);
                option.setDefaultValue(null);
                break;
            case FILE:
            case DYNAMIC_FILE:
            case FILE_URL:
                prepareFileTypes();
                if (option.getType() == OptionType.FILE || option.getType() == OptionType.DYNAMIC_FILE) {
                    option.setDefaultValue(null);
                }
                break;
            default:
                break;
        }

    }

    private void prepareValues() {
        if (CollectionUtils.isNullOrEmpty(valuesList)) {
            return;
        }

        Set<OptionEnumValue> preparedValues = new WithDuplicatesSet<OptionEnumValue>();

        for (int i = 0; i < valuesList.size(); i++) {
            OptionEnumValue value = valuesList.get(i);

            if (value != null) {
                if (defaultEnumValue != null) {
                    value.setDefault(i == defaultEnumValue);
                }
                preparedValues.add(value);
            }
        }

        valuesList = null;
        option.setValues(preparedValues);
    }

    private void prepareFileTypes() {
        List<OptionFileType> optionFileTypes = new ArrayList<OptionFileType>();

        if (selFileTypes != null) {
            Set<String> fileTypes = new LinkedHashSet<String>(getIdList(selFileTypes));

            for (String fileType : fileTypes) {
                OptionFileType fileTypesOption = new OptionFileType();
                fileTypesOption.setFileType(fileType);
                optionFileTypes.add(fileTypesOption);
            }
        }

        option.setFileTypes(optionFileTypes);
    }

    private List<String> getIdList(List<IdNameBean> beans) {
        if (beans == null || beans.isEmpty()) {
            return null;
        }

        List<String> res = new LinkedList<String>();

        for (IdNameBean bean : beans) {
            res.add(bean.getId());
        }

        return res;
    }


    /**
     * The only goal of this set it to allow duplicated elements inside.
     * It's not good from specification point of view, but allows keeping validation of set on a service layer.
     *
     * @param <T>
     */
    public class WithDuplicatesSet<T> extends AbstractSet<T> {
        private List<T> container = new ArrayList<T>();

        @Override
        public boolean add(T o) {
            return container.add(o);
        }

        @Override
        public int size() {
            return container.size();
        }

        @Override
        public Iterator<T> iterator() {
            return container.iterator();
        }
    }


    @Override
    public void invalid() throws Exception {
        OptionGroup optionGroup = option.getOptionGroup();
        if ((optionGroup.getId() != null)) {
            optionGroup = optionGroupService.findById(optionGroup.getId());
            option.setOptionGroup(optionGroup);
        }
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            setCreativeSizeId(optionGroup.getCreativeSize().getId());
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            setTemplateId(optionGroup.getTemplate().getId());
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (option.getId() != null) {
            final Option persistent = optionService.findById(option.getId());
            breadcrumbs = new OptionGroupBreadcrumbsBuilder().build(persistent.getOptionGroup())
                    .add(new OptionBreadcrumbsElement(persistent))
                    .add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = super.getBreadcrumbs();
            breadcrumbs.add("Option.entityName.new");
        }
        return breadcrumbs;
    }
}
