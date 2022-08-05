package com.foros.action.admin.option;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

public class ViewOptionGroupAction extends OptionGroupActionSupport {

    private Long id;

    @ReadOnly
    @Restrict(restriction="OptionGroup.view", parameters="find('OptionGroup', #target.id)")
    public String view() {
        optionGroup = optionGroupService.view(id);
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            setCreativeSizeId(optionGroup.getCreativeSize().getId());
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            setTemplateId(optionGroup.getTemplate().getId());
        }
        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getAvailabilityKey() {
        return getAvailabilities().get(getModel().getAvailability());
    }

    public String getCollapsabilityKey() {
        return getCollapsabilities().get(getModel().getCollapsability());
    }
}
