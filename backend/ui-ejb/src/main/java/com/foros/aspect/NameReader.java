package com.foros.aspect;

import java.lang.annotation.Annotation;

public interface NameReader<T extends Annotation> {

    String read(T annotation);

}
