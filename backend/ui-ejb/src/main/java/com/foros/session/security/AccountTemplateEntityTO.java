package com.foros.session.security;

import com.foros.model.LocalizableName;
import com.foros.session.EntityTO;

public class AccountTemplateEntityTO extends EntityTO {
    private long templateId;
    private LocalizableName templateName;

    public AccountTemplateEntityTO(Long id, String accountName, char status, Long templateId, String defaultName) {
        super(id, accountName, status);
        this.templateId = templateId;
        this.templateName = new LocalizableName(defaultName, "Template." + templateId);
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public LocalizableName getTemplateName() {
        return templateName;
    }

    public void setTemplateName(LocalizableName templateName) {
        this.templateName = templateName;
    }
}
