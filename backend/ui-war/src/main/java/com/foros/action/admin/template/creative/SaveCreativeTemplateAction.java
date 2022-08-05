package com.foros.action.admin.template.creative;

import com.foros.action.admin.template.SaveTemplateOptionsSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.template.CreativeTemplate;

import java.util.HashSet;
import java.util.Set;

public class SaveCreativeTemplateAction extends SaveTemplateOptionsSupport<CreativeTemplate> implements BreadcrumbsSupport {

    // model
    private CreativeTemplate creativeTemplate = new CreativeTemplate();

    public String create() {
        templateService.create(creativeTemplate);
        return SUCCESS;
    }

    public String update() {
        templateService.update(creativeTemplate);
        return SUCCESS;
    }

    @Override
    public CreativeTemplate getModel() {
        return creativeTemplate;
    }

    public void setSelectedCategories(Set<Long> categories) {
        creativeTemplate.setCategories(createCategories(categories));
    }

    private Set<CreativeCategory> createCategories(Set<Long> categories) {
        Set<CreativeCategory> result = new HashSet<CreativeCategory>();
        for (Long category : categories) {
            result.add(new CreativeCategory(category));
        }
        return result;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (creativeTemplate.getId() != null)
        {
            CreativeTemplate persistent = (CreativeTemplate) templateService.findById(creativeTemplate.getId());
            breadcrumbs.add(new CreativeTemplatesBreadcrumbsElement()).add(new CreativeTemplateBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        }

        return breadcrumbs;
    }
}
