package com.foros.action.admin.option;

import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionFileType;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.model.template.SubstitutionCategory;
import com.foros.model.template.Template;
import com.foros.session.ServiceLocator;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.template.OptionGroupService;
import com.foros.session.template.OptionService;
import com.foros.session.template.TemplateService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;


@Conversion()
public class OptionActionSupport extends BaseActionSupport implements ModelDriven<Option>, BreadcrumbsSupport {

    @EJB
    protected CreativeSizeService creativeSizeService;

    @EJB
    protected TemplateService templateService;

    @EJB
    protected OptionGroupService optionGroupService;

    @EJB
    protected OptionService optionService;

    private Long optionGroupId;
    private Long creativeSizeId;
    private Long templateId;
    private RelatedType relatedType;

    protected Option option = new Option();

    protected Long integerDefaultValue;
    protected List<OptionEnumValue> valuesList;
    protected Integer defaultEnumValue;
    protected List<IdNameBean> allFileTypes;
    protected List<IdNameBean> selFileTypes;

    private Set<OptionGroup> advertiserOptionGroups;
    private Set<OptionGroup> publisherOptionGroups;
    private Set<OptionGroup> hiddenOptionGroups;

    @TypeConversion(type = ConversionType.CLASS, converter = "com.foros.framework.conversion.LongNumberFormattingConverter")
    public Long getIntegerDefaultValue() {
        return integerDefaultValue != null? integerDefaultValue: Long.valueOf(option.getDefaultValue());
    }

    @TypeConversion(type = ConversionType.CLASS, converter = "com.foros.framework.conversion.LongNumberFormattingConverter")
    public void setIntegerDefaultValue(Long integerDefaultValue) {
        this.integerDefaultValue = integerDefaultValue;
    }

    public List<OptionEnumValue> getValuesList() {
        if (valuesList == null && option != null && !option.getValues().isEmpty()) {
            valuesList = new ArrayList<OptionEnumValue>(option.getValues());

            for (int i = 0; i < valuesList.size(); i++) {
                OptionEnumValue value = valuesList.get(i);
                if (value.isDefault()) {
                    defaultEnumValue = i;
                }
            }
        }

        return valuesList;
    }

    public void setValuesList(List<OptionEnumValue> valuesList) {
        this.valuesList = valuesList;
    }

    public Integer getDefaultEnumValue() {
        if (valuesList == null) {
            getValuesList();
        }

        return defaultEnumValue;
    }

    public void setDefaultEnumValue(Integer defaultEnumValue) {
        this.defaultEnumValue = defaultEnumValue;
    }

    public List<IdNameBean> getAllFileTypes() {
        if (allFileTypes == null) {
            allFileTypes = new ArrayList<IdNameBean>();
            List<String> allowedFileTypes = ServiceLocator.getInstance().lookup(ConfigService.class).get(ConfigParameters.ALLOWED_FILE_TYPES);
            for (String name : allowedFileTypes) {
                allFileTypes.add(new IdNameBean(name, name));
            }
        }

        return allFileTypes;
    }

    public List<IdNameBean> getSelFileTypes() {
        return selFileTypes;
    }

    public void setSelFileTypes(List<IdNameBean> fileTypes) {
        this.selFileTypes = fileTypes;
    }

    protected void populateFileTypes() {
        if (option.getFileTypes().isEmpty()) {
            return;
        }

        selFileTypes = new ArrayList<IdNameBean>();
        for (OptionFileType fileType : option.getFileTypes()) {
            selFileTypes.add(new IdNameBean(fileType.getFileType(), fileType.getFileType()));
        }
    }

    public OptionType[] getAvailableTypes() {
        return Option.getAllowedTypes();
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public Long getCreativeSizeId() {
        return creativeSizeId;
    }

    public void setCreativeSizeId(Long creativeSizeId) {
        this.creativeSizeId = creativeSizeId;
    }

    public CreativeSize getCreativeSize() {
        if (creativeSizeId == null) {
            return null;
        }
        return creativeSizeService.findById(creativeSizeId);
    }

    public Template getTemplate() {
        if (templateId == null) {
            return null;
        }
        return templateService.findById(templateId);
    }

    public RelatedType getRelatedType() {
        if (relatedType == null) {
            relatedType = OptionAndGroupHelper.getRelatedType(getTemplate(), getCreativeSize());
        }
        return relatedType;
    }

    @Override
    public Option getModel() {
        return option;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        final OptionGroup optionGroup = option.getOptionGroup();
        final Breadcrumbs breadcrumbs = new OptionGroupBreadcrumbsBuilder().build(optionGroup);
        if (option.getId() != null) {
            breadcrumbs.add(new OptionBreadcrumbsElement(option));
        }
        return breadcrumbs;
    }

    public boolean isAdvertiserTypeEnabled() {
        return OptionAndGroupHelper.isAdvertiserTypeEnabled(getRelatedType(), getModel().getId(), getModel().getOptionGroup() == null ? null : getModel().getOptionGroup().getType());
    }

    public boolean isPublisherTypeEnabled() {
        return OptionAndGroupHelper.isPublisherTypeEnabled(getModel().getId(), getModel().getOptionGroup() == null ? null : getModel().getOptionGroup().getType());
    }

    public boolean isHiddenTypeEnabled() {
        return OptionAndGroupHelper.isHiddenTypeEnabled(getModel().getId(), getModel().getOptionGroup() == null ? null : getModel().getOptionGroup().getType());
    }

    public Set<OptionGroup> getAdvertiserOptionGroups() {
        if (advertiserOptionGroups == null) {
            advertiserOptionGroups = OptionAndGroupHelper.getAdvertiserOptionGroups(getTemplate(), getCreativeSize());
        }
        return advertiserOptionGroups;
    }

    public Set<OptionGroup> getPublisherOptionGroups() {
        if (publisherOptionGroups == null) {
            publisherOptionGroups = OptionAndGroupHelper.getPublisherOptionGroups(getTemplate(), getCreativeSize());
        }
        return publisherOptionGroups;
    }

    public Set<OptionGroup> getHiddenOptionGroups() {
        if (hiddenOptionGroups == null) {
            hiddenOptionGroups = OptionAndGroupHelper.getHiddenOptionGroups(getTemplate(), getCreativeSize());
        }
        return hiddenOptionGroups;
    }

    public void setOptionGroupId(Long optionGroupId) {
        this.optionGroupId = optionGroupId;
    }

    public Long getOptionGroupId() {
        return optionGroupId;
    }

    public Collection<CreativeToken> getAdvertisersTokens() {
        return SubstitutionCategory.ADVERTISER.getTokens();
    }

    public Collection<CreativeToken> getPublisherTokens() {
        return SubstitutionCategory.PUBLISHER.getTokens();
    }

    public Collection<CreativeToken> getGenericTokens() {
        return SubstitutionCategory.GENERIC.getTokens();
    }

    public Collection<CreativeToken> getInternalTokens() {
        return SubstitutionCategory.INTERNAL.getTokens();
    }
}
