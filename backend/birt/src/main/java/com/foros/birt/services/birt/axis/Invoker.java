package com.foros.birt.services.birt.axis;

import org.eclipse.birt.report.soapengine.api.GetUpdatedObjects;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;

public interface Invoker {

    GetUpdatedObjectsResponse invoke(GetUpdatedObjects request) throws Exception;

}
