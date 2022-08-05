package com.foros.reporting.serializer;

import com.foros.reporting.Row;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.tools.ResultSerializerWrapper;
import com.foros.session.reporting.OutputType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.output.CountingOutputStream;

public class AuditResultHandlerWrapper extends ResultSerializerWrapper {


    private CountingOutputStream stream;
    private OutputType outputType;
    private long rowsCount = 0;
    private Set<DbColumn> columns = new LinkedHashSet<DbColumn>();

    public AuditResultHandlerWrapper(ResultSerializer resultHandler, OutputType outputType) {
        this(resultHandler, outputType, null);
    }

    public AuditResultHandlerWrapper(ResultSerializer resultHandler, OutputType outputType, CountingOutputStream stream) {
        super(resultHandler);
        this.outputType = outputType;
        this.stream = stream;
    }

    @Override
    public void before(MetaData metaData) {
        resultHandler.before(metaData);

        List<DbColumn> dbColumns = metaData.getColumns();
        columns.addAll(dbColumns);
    }

    @Override
    public void row(Row row) {
        resultHandler.row(row);
        rowsCount++;
    }

    public Long getSize() {
        return stream != null ? stream.getByteCount() : null;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public long getRowsCount() {
        return rowsCount;
    }

    public List<DbColumn> getColumns() {
        return new ArrayList<DbColumn>(columns);
    }
}
