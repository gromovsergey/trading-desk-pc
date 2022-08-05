package com.foros.aspect;

import java.lang.annotation.Annotation;

public interface AspectInfo {

    Annotation getAnnotation();

    Class<? extends Annotation> getIndex();

    boolean forProperty();

}
