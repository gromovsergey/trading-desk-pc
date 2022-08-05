package com.foros.action.admin.template.creative;

import com.foros.action.admin.template.TemplateModelSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Template;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.template.TemplateService;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;

public class EditCreativeTemplateAction extends TemplateModelSupport<CreativeTemplate> implements BreadcrumbsSupport {

    @EJB
    private TemplateService templateService;

    // model
    private CreativeTemplate creativeTemplate = new CreativeTemplate();

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction="Template.create")
    public String create() {
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Template.update", parameters="find('Template',#target.model.id)")
    public String edit() {
        Template template = templateService.view(creativeTemplate.getId());
        if (!(template instanceof CreativeTemplate)) {
            throw new EntityNotFoundException("Creative template with id=" + creativeTemplate.getId() + " not found");
        }
        this.creativeTemplate = (CreativeTemplate) template;
        breadcrumbs = new Breadcrumbs().add(new CreativeTemplatesBreadcrumbsElement()).add(new CreativeTemplateBreadcrumbsElement(creativeTemplate)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    public String createCopy() {
        creativeTemplate = (CreativeTemplate) templateService.createCopy(creativeTemplate.getId());
        return SUCCESS;
    }

    @Override
    public CreativeTemplate getModel() {
        return creativeTemplate;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
