package com.foros.action.admin.template.creative;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;
import com.foros.session.template.TemplateService;

import java.util.Set;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;

import com.opensymphony.xwork2.ModelDriven;

public class ViewCreativeTemplateAction extends BaseActionSupport implements ModelDriven<CreativeTemplate>, BreadcrumbsSupport {

    @EJB
    private TemplateService templateService;

    // model
    private CreativeTemplate creativeTemplate;

    // parameters
    private Long id;

    private boolean deleteError; //TODO move to validation
    private String templateFileId;

    @ReadOnly
    public String view() {
        Template template = templateService.view(id);
        if (!(template instanceof CreativeTemplate)) {
            throw new EntityNotFoundException("Creative template with id=" + id + " not found");
        }
        this.creativeTemplate = (CreativeTemplate) template;

        if (deleteError) {
            String msg = getText("CreativeTemplateFile.linked.delete");
            addFieldError("creativeTemplate.id." + templateFileId, msg);
        }

        return SUCCESS;
    }

    @Override
    public CreativeTemplate getModel() {
        return creativeTemplate;
    }

    public CreativeTemplate getEntity() {
        return getModel();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDeleteError() {
        return deleteError;
    }

    public void setDeleteError(boolean deleteError) {
        this.deleteError = deleteError;
    }

    public String getTemplateFileId() {
        return templateFileId;
    }

    public void setTemplateFileId(String templateFileId) {
        this.templateFileId = templateFileId;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CreativeTemplatesBreadcrumbsElement()).add(new CreativeTemplateBreadcrumbsElement(creativeTemplate));
    }
}
