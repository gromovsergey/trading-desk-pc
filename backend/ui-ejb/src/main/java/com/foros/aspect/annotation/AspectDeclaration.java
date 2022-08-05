package com.foros.aspect.annotation;

import com.foros.aspect.NameReader;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface AspectDeclaration {

    Class<? extends NameReader> reader();

    Class<? extends Annotation> index();

    String convention();

}
