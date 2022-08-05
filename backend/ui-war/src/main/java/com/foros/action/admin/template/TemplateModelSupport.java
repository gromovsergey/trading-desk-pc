package com.foros.action.admin.template;

import com.foros.action.BaseActionSupport;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.template.Template;
import com.foros.session.creative.CreativeCategoryService;
import com.foros.util.LocalizableNameUtil;

import java.util.Collection;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public abstract class TemplateModelSupport<T extends Template> extends BaseActionSupport implements ModelDriven<T> {

    @EJB
    private CreativeCategoryService categoryService;

    public Collection<CreativeCategory> getAvailableCategories() {
        return categoryService.findByType(CreativeCategoryType.VISUAL, true);
    }

    public String localize(LocalizableNameEntity entity) {
        return LocalizableNameUtil.getLocalizedValue(entity.getName());
    }

    public T getEntity() {
        return getModel();
    }
    
}
