package com.foros.reporting.tools.query.strategy;

import com.foros.reporting.ResultHandler;
import com.foros.reporting.rowsource.RowSource;

public interface IterationStrategy {

    void process(RowSource rowSource, ResultHandler handler);

}
