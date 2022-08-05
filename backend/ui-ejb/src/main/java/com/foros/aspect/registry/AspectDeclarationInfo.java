package com.foros.aspect.registry;

import java.lang.annotation.Annotation;
import java.util.regex.Pattern;

public class AspectDeclarationInfo {

    private Annotation annotation;
    private Class<? extends Annotation> index;
    private String name;
    private Pattern convention;

    public AspectDeclarationInfo(Annotation annotation, Class<? extends Annotation> index, String name, String convention) {
        this.annotation = annotation;
        this.index = index;
        this.name = name;
        this.convention = Pattern.compile(convention);
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Annotation> getIndex() {
        return index;
    }

    public Pattern getConvention() {
        return convention;
    }

}
