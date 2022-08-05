package com.foros.session.query;

import javax.ejb.Local;

@Local
public interface QueryExecutorService {

    QueryExecutor executor(BusinessQuery query);

}
