package com.foros.aspect.registry;

import com.foros.aspect.annotation.ElFunction;

import javax.ejb.Local;

@Local
public interface ElFunctionRegistryService {

    ElFunctionDescriptor getDescriptor(String name, ElFunction.Namespace namespace);

}
