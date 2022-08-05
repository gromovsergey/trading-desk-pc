package com.foros.birt.services.birt.axis;

import java.rmi.RemoteException;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjects;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.endpoint.BirtSoapPort;

public class BirtSoapPortStub implements BirtSoapPort {
    @Override
    public GetUpdatedObjectsResponse getUpdatedObjects(GetUpdatedObjects request) throws RemoteException {
        return null;
    }
}
