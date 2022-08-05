package com.foros.action.admin.template;

import com.foros.action.BaseActionSupport;
import com.foros.session.template.TemplateService;

import javax.ejb.EJB;

public class StatusTemplateAction extends BaseActionSupport {

    @EJB
    private TemplateService templateService;

    private Long id;

    public String delete() {
        templateService.delete(id);
        return SUCCESS;
    }

    public String undelete() {
        templateService.undelete(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
