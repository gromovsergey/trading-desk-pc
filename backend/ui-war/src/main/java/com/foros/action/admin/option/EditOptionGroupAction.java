package com.foros.action.admin.option;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Template;
import com.foros.restriction.annotation.Restrict;

public class EditOptionGroupAction extends OptionGroupActionSupport {
    private Long id;

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction="OptionGroup.create", parameters={"#target.templateId", "#target.creativeSizeId"})
    public String create() {
        if (getTemplateId() != null) {
            optionGroup.setTemplate(templateService.findById(getTemplateId()));
        } else if (getCreativeSizeId() != null) {
            optionGroup.setCreativeSize(creativeSizeService.findById(getCreativeSizeId()));
        }
        breadcrumbs = super.getBreadcrumbs().add("OptionGroup.entityName.new");
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="OptionGroup.update", parameters="find('OptionGroup', #target.id)")
    public String edit() {
        optionGroup = optionGroupService.view(id);
        populate();
        breadcrumbs = super.getBreadcrumbs().add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    private void populate() {
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            setCreativeSizeId(optionGroup.getCreativeSize().getId());
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            setTemplateId(optionGroup.getTemplate().getId());
        }
    }

    @Restrict(restriction="OptionGroup.delete", parameters="find('OptionGroup', #target.id)")
    public String remove() {
        optionGroup = optionGroupService.findById(id);
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            setCreativeSizeId(optionGroup.getCreativeSize().getId());
            optionGroupService.remove(id);
            return "creativeSize";
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            Template template = optionGroup.getTemplate();
            setTemplateId(template.getId());
            optionGroupService.remove(id);
            if (template instanceof CreativeTemplate) {
                return "creativeTemplate";
            } else if (template instanceof DiscoverTemplate) {
                return "discoverTemplate";
            }
        }
        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
