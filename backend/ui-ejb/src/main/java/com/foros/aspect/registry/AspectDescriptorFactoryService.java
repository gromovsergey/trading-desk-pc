package com.foros.aspect.registry;

import com.foros.aspect.AspectInfo;
import com.foros.aspect.ElAspectInfo;
import javax.ejb.Local;

@Local
public interface AspectDescriptorFactoryService {

    AspectDescriptor create(AspectInfo aspectInfo);

}
