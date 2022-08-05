package com.foros.action.admin.template.discover;

import com.foros.action.admin.template.TemplateModelSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.template.TemplateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;

public class EditDiscoverTemplateAction extends TemplateModelSupport<DiscoverTemplate> implements BreadcrumbsSupport {

    @EJB
    private TemplateService templateService;

    // model
    private DiscoverTemplate discoverTemplate = new DiscoverTemplate();
    private List<TemplateFile> files = new ArrayList<TemplateFile>();

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction="Template.create")
    public String create() {
        files.add(new TemplateFile());
        files.add(new TemplateFile());
        files.add(new TemplateFile());
        breadcrumbs = new Breadcrumbs().add(new DiscoverTemplatesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Template.update", parameters="find('Template',#target.model.id)")
    public String edit() {
        Template template = templateService.view(discoverTemplate.getId());
        if (!(template instanceof DiscoverTemplate)) {
            throw new EntityNotFoundException("Discover template with id=" + discoverTemplate.getId() + " not found");
        }
        this.discoverTemplate = (DiscoverTemplate) template;

        files = new ArrayList<TemplateFile>(discoverTemplate.getTemplateFiles());
        Collections.sort(files, new FilesComparator());
        breadcrumbs = new Breadcrumbs().add(new DiscoverTemplatesBreadcrumbsElement()).add(new DiscoverTemplateBreadcrumbsElement(discoverTemplate)).add(ActionBreadcrumbs.EDIT);

        return SUCCESS;
    }

    public String createCopy() {
        discoverTemplate = (DiscoverTemplate) templateService.createCopy(discoverTemplate.getId());
        return SUCCESS;
    }

    @Override
    public DiscoverTemplate getModel() {
        return discoverTemplate;
    }

    public List<TemplateFile> getFiles() {
        return files;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
