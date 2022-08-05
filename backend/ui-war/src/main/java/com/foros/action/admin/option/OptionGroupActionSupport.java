package com.foros.action.admin.option;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroup.Availability;
import com.foros.model.template.OptionGroup.Collapsability;
import com.foros.model.template.Template;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.template.OptionGroupService;
import com.foros.session.template.TemplateService;
import com.foros.util.CollectionUtils;

import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.conversion.annotations.Conversion;

@Conversion()
public abstract class OptionGroupActionSupport extends BaseActionSupport implements ModelDriven<OptionGroup>, BreadcrumbsSupport {
    @EJB
    protected CreativeSizeService creativeSizeService;
    @EJB
    protected TemplateService templateService;

    @EJB
    protected OptionGroupService optionGroupService;

    private Long creativeSizeId;
    private Long templateId;

    private Set<OptionGroup> advertiserOptionGroups;
    private Set<OptionGroup> publisherOptionGroups;
    private Set<OptionGroup> hiddenOptionGroups;

    private RelatedType relatedType;

    protected OptionGroup optionGroup = new OptionGroup();

    public Map<Availability, String> getAvailabilities() {
        return CollectionUtils
                .localizeMap(Availability.ALWAYS_ENABLED, "OptionGroup.availability.alwaysEnabled")
                .map(Availability.ENABLED_BY_DEFAULT, "OptionGroup.availability.enabledByDefault")
                .map(Availability.DISABLED_BY_DEFAULT, "OptionGroup.availability.disabledByDefault")
                .build();
    }

    public Map<Collapsability, String> getCollapsabilities() {
        return CollectionUtils
                .localizeMap(Collapsability.NOT_COLLAPSIBLE, "OptionGroup.collapsibility.notCollapsible")
                .map(Collapsability.COLLAPSED_BY_DEFAULT, "OptionGroup.collapsibility.collapsedByDefault")
                .map(Collapsability.EXPANDED_BY_DEFAULT, "OptionGroup.collapsibility.expandedByDefault")
                .build();
    }

    @Override
    public OptionGroup getModel() {
        return optionGroup;
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

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
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
    public Breadcrumbs getBreadcrumbs() {
        return new OptionGroupBreadcrumbsBuilder().build(optionGroup);
    }

    public boolean isAdvertiserTypeEnabled() {
        return OptionAndGroupHelper.isAdvertiserTypeEnabled(getRelatedType(), getModel().getId(), getModel().getType());
    }

    public boolean isPublisherTypeEnabled() {
        return OptionAndGroupHelper.isPublisherTypeEnabled(getModel().getId(), getModel().getType());
    }

    public boolean isHiddenTypeEnabled() {
        return OptionAndGroupHelper.isHiddenTypeEnabled(getModel().getId(), getModel().getType());
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
}
