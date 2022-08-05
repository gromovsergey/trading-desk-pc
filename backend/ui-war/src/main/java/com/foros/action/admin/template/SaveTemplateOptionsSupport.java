package com.foros.action.admin.template;

import com.foros.model.template.Template;
import com.foros.session.template.TemplateService;

import javax.ejb.EJB;

public abstract class SaveTemplateOptionsSupport<T extends Template> extends TemplateModelSupport<T> {

    @EJB
    protected TemplateService templateService;

}
