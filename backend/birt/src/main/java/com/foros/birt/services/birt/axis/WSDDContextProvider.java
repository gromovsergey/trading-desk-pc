package com.foros.birt.services.birt.axis;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;

public class WSDDContextProvider extends WSDDProvider {

    private Invoker invoker;

    public WSDDContextProvider(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public Handler newProviderInstance(WSDDService service, EngineConfiguration registry) throws Exception {
        return new ContextProvider(invoker);
    }

    @Override
    public String getName() {
        return "RPC-CONTEXT";
    }

}
