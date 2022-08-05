package com.foros.session.template;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.session.UtilityService;
import com.foros.session.creative.CreativeSizeRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class OptionGroupRestrictions {
    @EJB
    private UtilityService utilityService;

    @EJB
    private TemplateRestrictions templateRestrictions;

    @EJB
    private CreativeSizeRestrictions creativeSizeRestrictions;

    @Restriction
    public boolean canCreate(OptionGroup optionGroup) {
        if (optionGroup.getTemplate() != null && optionGroup.getTemplate().getId() != null) {
            return templateRestrictions.canUpdateOptions(utilityService.find(Template.class, optionGroup.getTemplate().getId()));
        } else if (optionGroup.getCreativeSize() != null && optionGroup.getCreativeSize().getId() != null) {
            return creativeSizeRestrictions.canUpdateOptions(utilityService.find(CreativeSize.class, optionGroup.getCreativeSize().getId()));
        }
        return false;
    }

    @Restriction
    public boolean canCreate(Long templateId, Long creativeSizeId) {
        if (templateId != null) {
            return templateRestrictions.canUpdateOptions(utilityService.find(Template.class, templateId));
        } else if (creativeSizeId != null) {
            return creativeSizeRestrictions.canUpdateOptions(utilityService.find(CreativeSize.class, creativeSizeId));
        }
        return false;
    }

    @Restriction
    public boolean canUpdate(OptionGroup optionGroup) {
        OptionGroup existing = utilityService.find(OptionGroup.class, optionGroup.getId());

        if (existing.getTemplate() != null) {
            return templateRestrictions.canUpdateOptions(existing.getTemplate());
        } else if (existing.getCreativeSize() != null) {
            return creativeSizeRestrictions.canUpdateOptions(existing.getCreativeSize());
        } else {
            return false;
        }
    }

    @Restriction
    public boolean canDelete(OptionGroup optionGroup) {
        OptionGroup existing = utilityService.find(OptionGroup.class, optionGroup.getId());

        if (existing.getTemplate() != null) {
            return templateRestrictions.canDeleteOptions(existing.getTemplate());
        } else if (existing.getCreativeSize() != null) {
            return creativeSizeRestrictions.canDeleteOptions(existing.getCreativeSize());
        } else {
            return false;
        }
    }

    @Restriction
    public boolean canView(OptionGroup optionGroup) {
        OptionGroup existing = utilityService.find(OptionGroup.class, optionGroup.getId());

        if (existing.getTemplate() != null) {
            return templateRestrictions.canView();
        } else if (existing.getCreativeSize() != null) {
            return creativeSizeRestrictions.canView();
        } else {
            return false;
        }
    }
}
