package com.foros.reporting.mock;

import com.foros.reporting.tools.olap.query.OlapQuery;
import com.foros.reporting.tools.olap.query.OlapQueryProvider;

public class OlapQueryProviderMock implements OlapQueryProvider {
    public OlapQuery query(String cube, Object context) {
        return null;
    }
}
