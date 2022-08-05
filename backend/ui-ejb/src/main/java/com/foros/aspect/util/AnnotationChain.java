package com.foros.aspect.util;

import java.lang.annotation.Annotation;
import java.util.LinkedList;

public class AnnotationChain<T extends Annotation> {

    private T bottom;
    private LinkedList<Annotation> chain = new LinkedList<Annotation>();

    public AnnotationChain(T bottom) {
        this.bottom = bottom;
        chain.add(bottom);
    }

    public AnnotationChain<T> chain(Annotation annotation) {
        chain.add(annotation);
        return this;
    }

    public T getBottom() {
        return bottom;
    }

    public Annotation getPreBottom() {
        return chain.get(1);
    }

    public Annotation getTop() {
        return chain.descendingIterator().next();
    }

    public LinkedList<Annotation> getChain() {
        return chain;
    }

    public <T extends Annotation> T find(Class<? extends Annotation> type) {
        for (Annotation annotation : chain) {
            if (type.isAssignableFrom(annotation.annotationType())) {
                return (T) annotation;
            }
        }

        return null;
    }

}
