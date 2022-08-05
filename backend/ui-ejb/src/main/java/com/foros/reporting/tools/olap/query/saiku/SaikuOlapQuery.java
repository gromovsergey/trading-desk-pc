package com.foros.reporting.tools.olap.query.saiku;

import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.rowsource.olap.saiku.SaikuCellSetRowSource;
import com.foros.reporting.rowsource.olap.saiku.SaikuCellSetValueReaderRegistry;
import com.foros.reporting.tools.olap.query.AbstractOlapQuery;
import com.foros.reporting.tools.CancelQueryService;
import com.foros.reporting.tools.CancelTask;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import com.phorm.oix.saiku.SaikuStatement;
import com.phorm.oix.saiku.cellset.SaikuCellSet;
import com.phorm.oix.saiku.utils.SaikuTimeoutException;

public class SaikuOlapQuery extends AbstractOlapQuery {
    private long expirationTime;

    private SaikuCellSetValueReaderRegistry readerRegistry = SaikuCellSetValueReaderRegistry.DEFAULT; // todo

    private SaikuStatement statement;

    private CancelQueryService cancelQueryService;

    public SaikuOlapQuery(CancelQueryService cancelQueryService, SaikuStatement statement, Object context, long expirationTime) {
        super(context, statement.getCube());
        this.cancelQueryService = cancelQueryService;
        this.statement = statement;
        this.expirationTime = expirationTime;
    }

    @Override
    public void execute(ResultHandler handler, IterationStrategy iterationStrategy) {
        SaikuCellSet cellSet = generateMdxAndExecute();
        iterationStrategy.process(new SaikuCellSetRowSource(cellSet, context, readerRegistry), handler);
    }

    private SaikuCellSet generateMdxAndExecute() {
        String mdx = generateMdx(statement.getUuid());
        CancelTask cancelTask = new CancelTask() {
            @Override
            public void cancel() {
                statement.close();
            }
        };
        try {
            cancelQueryService.registerCancelTask(cancelTask);
            checkCancelled();
            return statement.execute(mdx);
        } finally {
            cancelQueryService.unregisterCancelTask(cancelTask);
        }
    }

    @Override
    public void execute(ResultHandler handler, MetaData metaData) {
        execute(handler, new SimpleIterationStrategy(metaData));
    }

    protected void checkCancelled() {
        if (expirationTime <= System.currentTimeMillis()) {
            throw new SaikuTimeoutException("Saiku preparation timeout");
        }
        cancelQueryService.checkCancelled();
    }
}
