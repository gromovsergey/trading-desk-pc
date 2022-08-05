package com.foros.breadcrumbs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomParametersBreadcrumbsElement extends SimpleLinkBreadcrumbsElement {

    private Map<String, String> parameters = new HashMap<String, String>();

    public CustomParametersBreadcrumbsElement(String resource, String path) {
        super(resource, path);
    }

    protected void putParameter(String name, String value) {
        parameters.put(name, value);
    }

    protected void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParametersAsString() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = parameters.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            String value = parameters.get(name);
            builder.append(name).append("=").append(value);
            if (it.hasNext()) {
                builder.append("&");
            }
        }
        return builder.toString();
    }
}
