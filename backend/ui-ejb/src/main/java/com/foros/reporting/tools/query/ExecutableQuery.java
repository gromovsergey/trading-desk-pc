package com.foros.reporting.tools.query;

import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.tools.query.strategy.IterationStrategy;

public interface ExecutableQuery {

    void execute(ResultHandler handler, IterationStrategy iterationStrategy);

    void execute(ResultHandler handler, MetaData metaData);
}
