package com.foros.action.admin.template.discover;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;
import com.foros.session.template.TemplateService;

import java.util.Set;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;

import com.opensymphony.xwork2.ModelDriven;

public class ViewDiscoverTemplateAction extends BaseActionSupport implements ModelDriven<DiscoverTemplate>, BreadcrumbsSupport {

    @EJB
    private TemplateService templateService;

    // model
    private DiscoverTemplate discoverTemplate;

    // parameters
    private Long id;

    @ReadOnly
    public String view() {
        Template template = templateService.view(id);
        if (!(template instanceof DiscoverTemplate)) {
            throw new EntityNotFoundException("Discover template with id=" + id + " not found");
        }
        this.discoverTemplate = (DiscoverTemplate) template;
        return SUCCESS;
    }

    @Override
    public DiscoverTemplate getModel() {
        return discoverTemplate;
    }

    public DiscoverTemplate getEntity() {
        return getModel();
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new DiscoverTemplatesBreadcrumbsElement()).add(new DiscoverTemplateBreadcrumbsElement(discoverTemplate));
    }
}
