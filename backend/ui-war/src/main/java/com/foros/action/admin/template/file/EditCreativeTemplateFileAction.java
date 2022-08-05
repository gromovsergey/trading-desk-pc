package com.foros.action.admin.template.file;

import com.foros.action.admin.template.creative.CreativeTemplateBreadcrumbsElement;
import com.foros.action.admin.template.creative.CreativeTemplatesBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;

public class EditCreativeTemplateFileAction extends EditCreativeTemplateFileActionSupport implements BreadcrumbsSupport {

    private Long id;

    private Long templateId;

    private Template template;

    @ReadOnly
    public String create() {
        file = new TemplateFile();
        Template template = getTemplate();
        file.setTemplate(template);
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        file = templateService.findTemplateFileById(id);
        return SUCCESS;

    }

    private Template getTemplate() {
        if (template == null) {
            template = templateService.findById(templateId);
        }

        return template;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new CreativeTemplatesBreadcrumbsElement())
                .add(new CreativeTemplateBreadcrumbsElement(getTemplate()))
                .add(ActionBreadcrumbs.EDIT);
    }
}
