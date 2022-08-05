package com.foros.framework.struts;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import javax.servlet.ServletContext;
import org.apache.struts2.StrutsConstants;

public class StrutsSpringObjectFactory extends org.apache.struts2.spring.StrutsSpringObjectFactory {
    @Inject
    public StrutsSpringObjectFactory(
            @Inject(value = StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE, required = false) String autoWire,
            @Inject(value = StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE_ALWAYS_RESPECT, required = false) String alwaysAutoWire,
            @Inject(value = StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE, required = false) String useClassCacheStr,
            @Inject ServletContext servletContext,
            @Inject(StrutsConstants.STRUTS_DEVMODE) String devMode,
            @Inject Container container) {
        super(autoWire, alwaysAutoWire, useClassCacheStr, servletContext, devMode, container);
        // To fix OUI-22148, otherwise injectInternalBeans injects nothing.
        setContainer(container);
    }
}
