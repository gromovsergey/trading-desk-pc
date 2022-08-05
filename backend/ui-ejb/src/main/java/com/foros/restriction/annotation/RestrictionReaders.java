package com.foros.restriction.annotation;

import com.foros.aspect.AspectInfo;
import com.foros.aspect.AspectReader;
import com.foros.aspect.ElAspectInfo;
import com.foros.aspect.NameReader;
import com.foros.aspect.annotation.Aspect;
import com.foros.aspect.util.AnnotationChain;

public class RestrictionReaders {
    public static class RestrictionsNameReader implements NameReader<Restrictions> {

        @Override
        public String read(Restrictions annotation) {
            return annotation.value();
        }

    }

    public static class RestrictionNameReader implements NameReader<Restriction> {

        @Override
        public String read(Restriction annotation) {
            return annotation.value();
        }

    }

    public static class RestrictAspectReader implements AspectReader<Restrict> {

        @Override
        public AspectInfo read(Restrict annotation, AnnotationChain<Aspect> aspect) {
            return new ElAspectInfo(aspect.getTop(), aspect.getBottom().index(), annotation.restriction(), annotation.parameters());
        }

    }

}
