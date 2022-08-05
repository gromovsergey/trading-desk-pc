package com.foros.action.admin.template.discover;

import com.foros.action.admin.template.SaveTemplateOptionsSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.TemplateFile;
import com.foros.session.template.ApplicationFormatService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;

public class SaveDiscoverTemplateAction extends SaveTemplateOptionsSupport<DiscoverTemplate> implements BreadcrumbsSupport {

    @EJB
    private ApplicationFormatService applicationFormatService;

    // model
    private DiscoverTemplate discoverTemplate = new DiscoverTemplate();
    private List<TemplateFile> files = new ArrayList<TemplateFile>();

    public String create() {
        discoverTemplate.setTemplateFiles(fetchFiles());
        templateService.create(discoverTemplate);
        return SUCCESS;
    }

    public String update() {
        discoverTemplate.setTemplateFiles(fetchFiles());
        templateService.update(discoverTemplate);
        return SUCCESS;
    }

    @Override
    public DiscoverTemplate getModel() {
        return discoverTemplate;
    }

    private Set<TemplateFile> fetchFiles() {
        ApplicationFormat discoverTagFormat = applicationFormatService.findByName(ApplicationFormat.DISCOVER_TAG_FORMAT);
        ApplicationFormat discoverCustomizationFormat = applicationFormatService.findByName(ApplicationFormat.DISCOVER_CUSTOMIZATION_FORMAT);
        ApplicationFormat forosFormat = applicationFormatService.findByName(ApplicationFormat.PREVIEW_FORMAT);

        files.get(0).setApplicationFormat(discoverTagFormat);
        files.get(1).setApplicationFormat(discoverCustomizationFormat);
        files.get(2).setApplicationFormat(forosFormat);

        for (TemplateFile file : files) {
            file.setTemplate(getEntity());
        }

        return new HashSet<TemplateFile>(files);
    }

    public List<TemplateFile> getFiles() {
        return files;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (discoverTemplate.getId() != null) {
            DiscoverTemplate persistent = (DiscoverTemplate) templateService.findById(discoverTemplate.getId());
            breadcrumbs = new Breadcrumbs().add(new DiscoverTemplatesBreadcrumbsElement()).add(new DiscoverTemplateBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new DiscoverTemplatesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
