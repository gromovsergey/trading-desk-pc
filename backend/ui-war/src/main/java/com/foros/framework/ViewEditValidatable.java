package com.foros.framework;

/** 
 * Interface to be implemented by actions that need validation step for view/edit methods.
 */
public interface ViewEditValidatable {
    boolean viewValidate();

    boolean editValidate();
}
