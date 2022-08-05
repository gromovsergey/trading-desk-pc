package com.foros.action.admin.template.file;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.model.template.TemplateFile;
import com.foros.session.template.TemplateService;
import javax.ejb.EJB;

public class CreativeTemplateFileActionSupport extends BaseActionSupport implements ModelDriven<TemplateFile> {

    @EJB
    TemplateService templateService;

    TemplateFile file = new TemplateFile();

    @Override
    public TemplateFile getModel() {
        return file;
    }
}
