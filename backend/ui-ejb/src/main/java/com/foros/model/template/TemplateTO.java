package com.foros.model.template;

import com.foros.model.Status;
import com.foros.session.DisplayStatusEntityTO;
import com.foros.util.i18n.LocalizableNameProvider;

public class TemplateTO extends DisplayStatusEntityTO {

    public TemplateTO() {
    }

    public TemplateTO(Template template) {
        this(template.getId(), template.getDefaultName(), template.getStatus().getLetter());
    }

    public TemplateTO(Long id, String name, char status) {
        super(id, name, status, CreativeTemplate.getDisplayStatus(Status.valueOf(status)));
    }

    protected String getProvidedResKey() {
        return LocalizableNameProvider.TEMPLATE.getResourceKey(getId());
    }

}
