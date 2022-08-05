package com.foros.aspect.registry;

import java.lang.annotation.Annotation;
import javax.ejb.Local;

@Local
public interface AspectDeclarationRegistry {

    /**
     * @param name name of restriction
     * @return descriptor for restriction with passed name
     * @throws com.foros.aspect.AspectException if descriptor can not be found
     */
    AspectDeclarationDescriptor getDescriptor(Class<? extends Annotation> type, String name);

}
