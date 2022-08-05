package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.isp.Colocation;
import com.foros.rs.client.model.isp.ColocationSelector;

public class ColocationService extends ReadonlyServiceSupport<ColocationSelector, Colocation> {
    public ColocationService(RsClient rsClient) {
        super(rsClient, "/colocations");
    }
}
