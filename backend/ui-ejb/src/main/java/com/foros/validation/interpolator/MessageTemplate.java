package com.foros.validation.interpolator;

import java.io.Serializable;

public class MessageTemplate implements Serializable {
    private static final Object[] EMPTY_PARAMETERS = new Object[0];

    private String template;
    private Object[] parameters;

    public MessageTemplate(String template) {
        this(template, EMPTY_PARAMETERS);
    }

    public MessageTemplate(String template, Object[] parameters) {
        this.template = template;
        this.parameters = parameters;
    }

    public String getTemplate() {
        return template;
    }

    public Object[] getParameters() {
        return parameters;
    }

}
