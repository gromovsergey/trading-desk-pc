package com.foros.session.reporting;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.TooManyRowsException;
import com.foros.reporting.meta.AggregatableColumn;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.tools.ResultSerializerWrapper;
import com.foros.reporting.tools.subtotal.SubTotalHandlerWrapper;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.session.channel.service.TotalByTriggerTypeTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SummarySerializerWrapper extends ResultSerializerWrapper {

    private MetaData<? extends AggregatableColumn> metaData;
    private SummaryRow summaryRow = new SummaryRow();

    public SummarySerializerWrapper(ResultSerializer resultHandler, ReportMetaData<? extends AggregatableColumn> metaData) {
        super(resultHandler);
        this.metaData = metaData;
        this.summaryRow.addColumns(metaData.getColumns());
    }

    @Override
    public void row(Row row) {
        try {
            super.row(row);
        } catch (TooManyRowsException e) {
            summaryRow.processRow(row);
            throw e;
        }
        if (!row.getType().getName().startsWith("subtotal")) {
            summaryRow.processRow(row);
        }
    }

    @Override
    public void after() {
        if (summaryRow.isFilled()) {
            super.summary(metaData, summaryRow);
        }
        super.after();
    }

    private static class SummaryRow implements Row {

        private Map<Column, AggregateFunction> functions = new LinkedHashMap<>();

        private boolean filled;

        public void addColumns(List<? extends AggregatableColumn> columns) {
            for (AggregatableColumn column : columns) {
                functions.put(column, column.newAggregateFunction());
            }
        }

        @Override
        public Object get(Column column) {
            return functions.get(column).getValue();
        }

        public void processRow(Row row) {
            for (AggregateFunction function : functions.values()) {
                function.aggregate(row);
                filled = true;
            }
        }

        @Override
        public RowType getType() {
            return RowTypes.summary();
        }

        public boolean isFilled() {
            return filled;
        }
    }
}
