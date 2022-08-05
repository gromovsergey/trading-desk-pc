package com.foros.reporting.tools.subtotal;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.TooManyRowsException;
import com.foros.reporting.meta.AggregatableColumn;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.tools.ResultHandlerWrapper;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.NullAggregateFunction;
import com.foros.reporting.tools.subtotal.predicates.GroupingPredicate;
import com.foros.reporting.tools.subtotal.predicates.SingleColumnGroupingPredicate;
import com.foros.reporting.tools.subtotal.predicates.TotalGroupingPredicate;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubTotalHandlerWrapper extends ResultHandlerWrapper {

    private List<? extends Column> groupColumns;
    private Set<? extends AggregatableColumn> aggregateColumns;
    private List<Total> totals;
    private boolean doAfter = false;

    public SubTotalHandlerWrapper(ResultSerializer resultHandler, List<? extends Column> groupColumns, Set<? extends AggregatableColumn> aggregateColumns) {
        super(resultHandler);
        this.groupColumns = groupColumns;
        this.aggregateColumns = aggregateColumns;
    }

    @Override
    public void before(MetaData metaData) {
        super.before(metaData);

        List<Column> columns = new ArrayList<>(metaData.getColumns());
        columns.addAll(CollectionUtils.subtract(((ReportMetaData<OlapColumn>) metaData).getColumnsMeta().getColumnsWithDependencies(), columns));
        totals = new ArrayList<Total>(groupColumns.size() + 1);

        GroupingPredicate predicate = new TotalGroupingPredicate();

        for (Column groupColumn : groupColumns) {
            predicate = new SingleColumnGroupingPredicate(groupColumn, predicate);
            totals.add(newContext(groupColumn, predicate, columns));
        }
    }

    private Total newContext(Column groupColumn, GroupingPredicate predicate, List<Column> columns) {
        Total context = new Total(RowTypes.subTotal(groupColumn));
        context.predicate = predicate;

        Iterator<Column> it = columns.iterator();
        boolean afterGroup = false;

        while (it.hasNext()) {
            Column column =  it.next();
            if (!afterGroup) {
                context.add(column, new LastValueAggregateFunction(column));
                if (column.equals(groupColumn)) {
                    afterGroup = true;
                    context.add(it.next(), new LastValueAggregateFunction(column));
                }
            } else {
                if (aggregateColumns.contains(column)) {
                    context.add(column, ((AggregatableColumn)column).newAggregateFunction());
                } else {
                    context.add(column, NullAggregateFunction.instance());
                }
            }
        }
        return context;
    }

    @Override
    public void row(Row row) {
        doAfter = true;
        try {
            for (Total total : totals) {
                total.prepareRow(row);
            }

            for (int i = totals.size() - 1; i >= 0; i--) {
                Total total = totals.get(i);
                total.processRow(row);
            }

            super.row(row);
        } catch (TooManyRowsException e) {
            doAfter = false;
            throw e;
        }
    }

    @Override
    public void after() {
        if (doAfter) {
            for (int i = totals.size() - 1; i >= 0; i--) {
                super.row(totals.get(i));
            }
        }
        super.after();
    }

    private class Total implements Row {
        private RowType rowType;

        private Map<Column, AggregateFunction> functionsByColumn = new HashMap<Column, AggregateFunction>();
        private List<AggregateFunction> functions = new ArrayList<AggregateFunction>();
        private GroupingPredicate predicate;

        private Total(RowType rowType) {
            this.rowType = rowType;
        }


        @Override
        public Object get(Column column) {
            return functionsByColumn.get(column).getValue();
        }

        @Override
        public RowType getType() {
            return rowType;
        }

        public void add(Column column, AggregateFunction function) {
            functionsByColumn.put(column, function);
            functions.add(function);
        }

        public void prepareRow(Row row) {
            predicate.checkRow(row);
        }

        public void processRow(Row row) {
            boolean isNewGroup = predicate.isNewGroup();

            if (isNewGroup) {
                SubTotalHandlerWrapper.super.row(this);
                for (AggregateFunction function : functions) {
                    function.clean();
                }
            }

            for (AggregateFunction function : functions) {
                function.aggregate(row);
            }
        }
    }
}
