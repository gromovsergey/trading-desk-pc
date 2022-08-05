package com.foros.action.admin.option;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;


public class SaveOptionGroupAction extends OptionGroupActionSupport {
    private Long position;

    public String create() {
        if (getCreativeSizeId() != null) {
            CreativeSize creativeSize = creativeSizeService.findById(getCreativeSizeId());
            optionGroup.setCreativeSize(creativeSize);
        }
        if (getTemplateId() != null) {
            Template template = templateService.findById(getTemplateId());
            optionGroup.setTemplate(template);
        }
        optionGroupService.create(optionGroup);
        return SUCCESS;
    }

    public String update() {
        optionGroupService.update(optionGroup);
        return SUCCESS;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (optionGroup.getId() != null) {
            final OptionGroup persistent = optionGroupService.findById(optionGroup.getId());
            breadcrumbs = new OptionGroupBreadcrumbsBuilder().build(persistent)
            .add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = super.getBreadcrumbs();
            breadcrumbs.add("OptionGroup.entityName.new");
        }
        return breadcrumbs;
    }

}
