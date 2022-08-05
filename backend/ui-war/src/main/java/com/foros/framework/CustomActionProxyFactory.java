package com.foros.framework;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;

import java.util.List;
import java.util.Map;

public class CustomActionProxyFactory extends DefaultActionProxyFactory {
    @Override
    public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
        ActionProxy proxy = super.createActionProxy(namespace, actionName, extraContext, executeResult, cleanupContext);

        List<InterceptorMapping> interceptors = proxy.getConfig().getInterceptors();
        for (InterceptorMapping i : interceptors) {
            if ("csrf".equals(i.getName())) {
                return proxy;
            }
        }

        throw new ConfigurationException("Invocation for namespace: " + namespace + " action: " + actionName + " has no csrf interceptor."); 
    }
}
