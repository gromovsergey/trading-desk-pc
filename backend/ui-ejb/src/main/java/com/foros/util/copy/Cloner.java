package com.foros.util.copy;

/**
 * @author oleg_roshka
 */
public interface Cloner {
    Object clone(Object bean, ClonerContext context);
}
