package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.conversion.Conversion;
import com.foros.rs.client.model.advertising.conversion.ConversionSelector;

public class ConversionService extends EntityServiceSupport<Conversion, ConversionSelector> {
    public ConversionService(RsClient rsClient) {
        super(rsClient, "/conversions");
    }
}
