package com.foros.aspect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElAspectInfo extends AbstractAspectInfo {

    private String name;
    private List<String> parameters;

    public ElAspectInfo(Annotation annotation, Class<? extends Annotation> index, String name, String[] parameters) {
        super(annotation, index);
        this.name = name;
        this.parameters = Collections.unmodifiableList(Arrays.asList(parameters));
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public boolean forProperty() {
        return false;
    }

}
