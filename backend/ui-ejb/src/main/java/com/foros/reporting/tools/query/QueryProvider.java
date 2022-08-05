package com.foros.reporting.tools.query;

import com.foros.reporting.tools.query.cache.CachedQuery;
import com.foros.reporting.tools.template.SqlTemplateSupport;

import java.util.List;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;

public interface QueryProvider {

    SqlTemplateSupport createFunctionTemplate(String function);

    SqlTemplateSupport createCallableTemplate(String procedure);

    Query query(ResultSetExecutor executor);

    Query queryCallable(String procedure);

    Query queryFunction(String function);

    CachedQuery cached(Query query, String regionName);

    void execute(ResultSetExecutor executor, List<SqlParameterValue> parameters, ResultSetExtractor resultSetExtractor);
}
