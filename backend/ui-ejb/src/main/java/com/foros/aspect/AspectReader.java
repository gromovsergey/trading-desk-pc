package com.foros.aspect;

import com.foros.aspect.annotation.Aspect;

import com.foros.aspect.util.AnnotationChain;
import java.lang.annotation.Annotation;

public interface AspectReader<T extends Annotation> {

    AspectInfo read(T annotation, AnnotationChain<Aspect> aspect);

}
