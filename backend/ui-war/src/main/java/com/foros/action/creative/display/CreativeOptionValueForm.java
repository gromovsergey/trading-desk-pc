package com.foros.action.creative.display;

import com.foros.action.site.OptionValueForm;
import com.foros.model.creative.TextCreativeOption;

public class CreativeOptionValueForm extends OptionValueForm {
    private String label;
    private String fileUrl;
    private TextCreativeOption textOption;
    private String mandatory;

    public CreativeOptionValueForm() {
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public TextCreativeOption getTextOption() {
        return textOption;
    }

    public void setTextOption(TextCreativeOption textOption) {
        this.textOption = textOption;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
