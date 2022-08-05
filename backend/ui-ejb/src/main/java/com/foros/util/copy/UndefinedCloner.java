package com.foros.util.copy;

/**
 *
 * @author oleg_roshka
 */
public class UndefinedCloner implements Cloner {
    /** 
     * Creates a new instance of UndefinedCloner 
     */
    public UndefinedCloner() {
    }

    public Object clone(Object bean, ClonerContext context) {
        throw new UnsupportedOperationException("instances of " +
                UndefinedCloner.class + " should not be created");
    }
}
