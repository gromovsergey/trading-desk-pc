package com.foros.validation.annotation;

import com.foros.aspect.AspectInfo;
import com.foros.aspect.AspectReader;
import com.foros.aspect.ElAspectInfo;
import com.foros.aspect.NameReader;
import com.foros.aspect.ValidatorAspectInfo;
import com.foros.aspect.annotation.Aspect;
import com.foros.aspect.util.AnnotationChain;

public class ValidationReaders {
    public static class ValidationsNameReader implements NameReader<Validations> {

        @Override
        public String read(Validations annotation) {
            return annotation.value();
        }

    }

    public static class ValidationNameReader implements NameReader<Validation> {

        @Override
        public String read(Validation annotation) {
            return annotation.value();
        }

    }

    public static class ValidateAspectReader implements AspectReader<Validate> {

        @Override
        public AspectInfo read(Validate annotation, AnnotationChain<Aspect> aspect) {
            return new ElAspectInfo(aspect.getTop(), aspect.getBottom().index(), annotation.validation(), annotation.parameters());
        }

    }

    public static class ValidatorAspectReader implements AspectReader<Validator> {

        @Override
        public AspectInfo read(Validator annotation, AnnotationChain<Aspect> aspect) {
            return new ValidatorAspectInfo(aspect.getTop(), aspect.getBottom().index(), annotation.factory());
        }

    }

}
