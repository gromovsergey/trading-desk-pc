package com.foros.session.template;

import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.UtilityService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class OptionRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private UtilityService utilityService;

    @EJB
    private OptionGroupRestrictions optionGroupRestrictions;

    @Restriction
    public boolean canCreate(Option option) {
        OptionGroup group = utilityService.findById(OptionGroup.class, option.getOptionGroup().getId());
        if (group == null) {
            return false;
        }
        return optionGroupRestrictions.canCreate(group);
    }

    @Restriction
    public boolean canCreate(Long templateId, Long creativeSizeId, Long optionGroupId) {
        if (optionGroupId != null) {
            OptionGroup optionGroup = utilityService.findById(OptionGroup.class, optionGroupId);
            if (optionGroup == null) {
                return false;
            }
            return optionGroupRestrictions.canCreate(optionGroup);
        }
        return optionGroupRestrictions.canCreate(templateId, creativeSizeId);
    }

    @Restriction
    public boolean canUpdate(Option option) {
        OptionGroup group = utilityService.findById(OptionGroup.class, option.getOptionGroup().getId());
        if (group == null) {
            return false;
        }
        return optionGroupRestrictions.canUpdate(group);
    }

    @Restriction
    public boolean canDelete(Option option) {
        OptionGroup group = utilityService.findById(OptionGroup.class, option.getOptionGroup().getId());
        if (group == null) {
            return false;
        }
        return optionGroupRestrictions.canDelete(group);
    }

    @Restriction
    public boolean canView(Option option) {
        OptionGroup group = utilityService.findById(OptionGroup.class, option.getOptionGroup().getId());
        if (group == null) {
            return false;
        }
        return optionGroupRestrictions.canView(group);
    }
}
