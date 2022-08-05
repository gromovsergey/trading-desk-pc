package com.foros.reporting.tools.query.cache;

import com.foros.cache.generic.CacheRegion;
import com.foros.reporting.ReportingException;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.Row;
import com.foros.reporting.TooManyRowsException;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportColumnsMetaData;
import java.util.ArrayList;
import java.util.List;

class CacheResultHandler implements ResultHandler {

    private final CacheRegion cacheRegion;
    private List<Object> tags;
    private final Object key;
    private boolean exceptionWasThrown;
    private int maxCachedRows;
    private int counter;

    private ResultHandler handler;

    private List<Column> columns;
    private CacheRowSource result = new CacheRowSource();

    CacheResultHandler(CacheRegion cacheRegion, Object key, List<Object> tags, ResultHandler handler, int maxCachedRows) {
        this.cacheRegion = cacheRegion;
        this.key = key;
        this.tags = tags;
        this.handler = handler;
        this.maxCachedRows = maxCachedRows;
    }

    @Override
    public void before(MetaData metaData) {
        if(metaData.getColumnsMeta() instanceof ReportColumnsMetaData) {
            ReportColumnsMetaData columnsMeta = (ReportColumnsMetaData) metaData.getColumnsMeta();
            columns = new ArrayList<Column>(columnsMeta.getColumnsWithDependencies());
        } else {
            columns = new ArrayList<Column>(metaData.getColumns());
        }

        result.setColumns(columns);
        handler.before(metaData);
    }

    @Override
    public void row(Row row) {
        counter++;
        result.add(row.getType(), fetchValues(row));
        try {
            if (!exceptionWasThrown) {
                handler.row(row);
            }
        } catch (TooManyRowsException e) {
            exceptionWasThrown = true;
        }

        if (counter > maxCachedRows) {
            throw new TooManyRowsException(counter);
        }
    }

    private Object[] fetchValues(Row row) {
        Object[] result = new Object[columns.size()];

        int index = 0;
        for (Column column : columns) {
            result[index] = row.get(column);
            index++;
        }

        return result;
    }

    @Override
    public void after() {
        handler.after();
        cacheRegion.set(key, result, tags);
    }

    @Override
    public void close() {
        handler.close();
    }

    @Override
    public void onError(ReportingException ex) {
        handler.onError(ex);
    }
}
