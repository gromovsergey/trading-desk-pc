package com.foros.aspect;

import java.lang.annotation.Annotation;

public abstract class AbstractAspectInfo implements AspectInfo {

    protected Annotation annotation;
    protected Class<? extends Annotation> index;

    public AbstractAspectInfo(Annotation annotation, Class<? extends Annotation> index) {
        this.index = index;
        this.annotation = annotation;
    }

    @Override
    public Annotation getAnnotation() {
        return annotation;
    }

    @Override
    public Class<? extends Annotation> getIndex() {
        return index;
    }

}
