package com.foros.action.admin;

import com.foros.model.admin.DynamicResource;

import java.util.Map;
import java.util.regex.Pattern;

public class HtmlNameResourceAction extends EntityResourceAction {
    private static final Pattern namePattern = Pattern.compile("^[^<>&]*$");

    @Override
    public Long getMaxLength() {
        return 200L;
    }
    
    @Override
    public void validate() {
        super.validate();

        for (Map.Entry<String, DynamicResource> entry : getValues().entrySet()) {
            if (!namePattern.matcher(entry.getValue().getValue()).matches()) {
                addFieldError("value_" + entry.getKey(), getText("errors.field.illegalSymbols",
                        new String[]{"< > &"}));
            }
        }
    }
}
