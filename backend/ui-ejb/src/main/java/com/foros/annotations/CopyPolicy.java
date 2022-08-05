package com.foros.annotations;

import com.foros.util.copy.Cloner;
import com.foros.util.copy.UndefinedCloner;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CopyPolicy {
    CopyStrategy strategy() default CopyStrategy.DEEP;
    Class<? extends Cloner> cloner() default UndefinedCloner.class;
    Class type() default Void.class;
    boolean mandatory() default false;
} 
