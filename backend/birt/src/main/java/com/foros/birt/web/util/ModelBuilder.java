package com.foros.birt.web.util;

import java.util.HashMap;
import java.util.Map;

public class ModelBuilder {

    private Map<String, Object> model = new HashMap<String, Object>();

    public ModelBuilder add(String name, Object value) {
        model.put(name, value);
        return this;
    }

    public Map<String, Object> build() {
        return model;
    }

    public static ModelBuilder create(String name, Object value) {
        return new ModelBuilder().add(name, value);
    }

}
