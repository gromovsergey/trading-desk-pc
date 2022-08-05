package com.foros.action.admin.option;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.model.template.OptionGroup;
import com.foros.restriction.annotation.Restrict;

public class EditOptionAction extends OptionActionSupport {
    private Long id;
    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction="Option.create", parameters={"#target.templateId", "#target.creativeSizeId", "#target.optionGroupId"})
    public String create() {
        OptionGroup optionGroup = null;
        if (getOptionGroupId() != null) {
            optionGroup = optionGroupService.findById(getOptionGroupId());
            setTemplateId(optionGroup.getTemplate() == null ? null : optionGroup.getTemplate().getId());
            setCreativeSizeId(optionGroup.getCreativeSize() == null ? null : optionGroup.getCreativeSize().getId());
            getModel().setOptionGroup(optionGroup);
        } else if (getTemplateId() != null) {
            optionGroup = new OptionGroup();
            optionGroup.setTemplate(templateService.findById(getTemplateId()));
        } else if (getCreativeSizeId() != null) {
            optionGroup = new OptionGroup();
            optionGroup.setCreativeSize(creativeSizeService.findById(getCreativeSizeId()));
        }

        breadcrumbs = new OptionGroupBreadcrumbsBuilder().build(optionGroup);
        breadcrumbs.add("Option.entityName.new");
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        option = optionService.view(id);
        OptionGroup optionGroup = option.getOptionGroup();
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            setCreativeSizeId(optionGroup.getCreativeSize().getId());
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            setTemplateId(optionGroup.getTemplate().getId());
        }
        populateFileTypes();
        breadcrumbs = super.getBreadcrumbs().add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    public String remove() {
        option = optionService.findById(id);
        optionService.remove(id);
        return SUCCESS;
    }

    public String createCopy() {
        option = optionService.createCopy(id);
        option.setToken(null);
        OptionGroup optionGroup = option.getOptionGroup();
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            setCreativeSizeId(optionGroup.getCreativeSize().getId());
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            setTemplateId(optionGroup.getTemplate().getId());
        }
        populateFileTypes();

        breadcrumbs = new OptionGroupBreadcrumbsBuilder().build(optionGroup);
        breadcrumbs.add("Option.entityName.new");
        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
