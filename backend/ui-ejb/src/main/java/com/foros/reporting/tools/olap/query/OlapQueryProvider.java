package com.foros.reporting.tools.olap.query;

import javax.ejb.Local;

@Local
public interface OlapQueryProvider {

    OlapQuery query(String cube, Object context);

}
