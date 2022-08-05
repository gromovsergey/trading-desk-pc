package com.foros.action.admin;

import com.foros.model.admin.DynamicResource;

import java.util.Map;
import java.util.regex.Pattern;

public class CategoryChannelResourceAction extends EntityResourceAction {
    private static final Pattern namePattern = Pattern.compile("^[^\\[\\]\\|<>]*$");

    @Override
    protected String getFinalKey() {
        return getEntityName() + "." + getResourceKey();
    }

    @Override
    public void validate() {
        super.validate();

        for (Map.Entry<String, DynamicResource> entry : getValues().entrySet()) {
            if (!namePattern.matcher(entry.getValue().getValue()).matches()) {
                addFieldError("value_" + entry.getKey(), getText("errors.field.illegalSymbols",
                        new String[]{"[ ] | < >"}));
            }
        }
    }
}
