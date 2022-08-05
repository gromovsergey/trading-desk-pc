package com.foros.model.template;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TemplateOptionPK implements Serializable {
    @Column(name = "OPTION_ID", nullable = false)
    private long optionId;

    @Column(name = "TEMPLATE_ID", nullable = false)
    private long templateId;

    public TemplateOptionPK() {

    }

    public TemplateOptionPK(long optionId, long templateId) {
        this.optionId = optionId;
        this.templateId = templateId;
    }

    public long getOptionId() {
        return optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemplateOptionPK that = (TemplateOptionPK) o;

        if (optionId != that.optionId) return false;
        if (templateId != that.templateId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (optionId ^ (optionId >>> 32));
        result = 31 * result + (int) (templateId ^ (templateId >>> 32));
        return result;
    }
}
