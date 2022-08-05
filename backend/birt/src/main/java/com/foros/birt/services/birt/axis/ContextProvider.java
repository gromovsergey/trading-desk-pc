package com.foros.birt.services.birt.axis;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import org.apache.axis.MessageContext;
import org.apache.axis.providers.java.RPCProvider;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjects;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.endpoint.BirtSoapPort;

public class ContextProvider extends RPCProvider {

    private Invoker invoker;

    public ContextProvider(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    protected Object makeNewServiceObject(MessageContext msgContext, String clsName) throws Exception {
        return new BirtSoapPortStub();
    }

    @Override
    protected Object invokeMethod(MessageContext msgContext, Method method, Object obj, Object[] argValues) throws Exception {
        return invoker.invoke((GetUpdatedObjects) argValues[0]);
    }

}
