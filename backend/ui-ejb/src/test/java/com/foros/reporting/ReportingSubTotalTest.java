package com.foros.reporting;

import com.foros.reporting.meta.AggregatableColumn;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DependentColumn;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaDataImpl;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.ResultSerializerSupport;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.tools.subtotal.SubTotalHandlerWrapper;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunctionFactory;
import com.foros.reporting.tools.subtotal.aggreagate.NullAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.SumAggregateFunction;
import group.Report;
import group.Unit;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Category({ Unit.class, Report.class })
public class ReportingSubTotalTest extends Assert {

    @Test
    public void testSubTotalWrapper() {
        List<Object[]> items = createItems();

        List<TestColumns> groupColumns = Arrays.asList(TestColumns.ADVERTISER, TestColumns.CAMPAIGN);
        Set<TestColumns> aggColumns = new HashSet<TestColumns>(Arrays.asList(TestColumns.VALUE, TestColumns.VALUE2));

        TestHandler testHandler = new TestHandler();
        SubTotalHandlerWrapper wrapper = new SubTotalHandlerWrapper(testHandler, groupColumns, aggColumns);

        wrapper.before(TestColumns.metaData());
        for (final Object[] item : items) {
            wrapper.row(new TestColumnsRow(item));
        }
        wrapper.after();

        List<Object[]> data = testHandler.getData();
        List<Object[]> expected = createExpected();
        assertEquals(expected.size(), data.size());
        for (int i = 0; i < expected.size(); i++) {
            Object[] expectedRow = expected.get(i);
            Object[] dataRow = data.get(i);
            assertTrue("Row " + i, ArrayUtils.isEquals(expectedRow, dataRow));
        }
    }

    private List<Object[]> createItems() {
        List<Object[]> items = new ArrayList<Object[]>();
        items.add(new Object[]{"Advertiser 1", "Campaign 1", "UK", new BigDecimal(1000), new BigDecimal("10")});
        items.add(new Object[]{"Advertiser 1", "Campaign 2", "UK", new BigDecimal(1000), new BigDecimal("10")});
        items.add(new Object[]{"Advertiser 1", "Campaign 2", "BR", new BigDecimal(2000), new BigDecimal("20")});
        items.add(new Object[]{"Advertiser 2", "Campaign 3", "US", new BigDecimal(3000), new BigDecimal("30")});
        items.add(new Object[]{"Advertiser 3", "Campaign 4", "BR", new BigDecimal(4000), new BigDecimal("40")});
        return items;
    }

    private List<Object[]> createExpected() {
        List<Object[]> items = new ArrayList<Object[]>();
        items.add(new Object[]{"Advertiser 1", "Campaign 1",   "UK",         new BigDecimal("1000"), new BigDecimal("10")});
        items.add(new Object[]{"Advertiser 1", "Campaign 1",   "Campaign 1", new BigDecimal("1000"), new BigDecimal("10")});

        items.add(new Object[]{"Advertiser 1", "Campaign 2",   "UK",         new BigDecimal("1000"), new BigDecimal("10")});
        items.add(new Object[]{"Advertiser 1", "Campaign 2",   "BR",         new BigDecimal("2000"), new BigDecimal("20")});
        items.add(new Object[]{"Advertiser 1", "Campaign 2",   "Campaign 2", new BigDecimal("3000"), new BigDecimal("30")});

        items.add(new Object[]{"Advertiser 1", "Advertiser 1",  null,        new BigDecimal("4000"), new BigDecimal("40")});

        items.add(new Object[]{"Advertiser 2", "Campaign 3",   "US",         new BigDecimal("3000"), new BigDecimal("30")});
        items.add(new Object[]{"Advertiser 2", "Campaign 3",   "Campaign 3", new BigDecimal("3000"), new BigDecimal("30")});

        items.add(new Object[]{"Advertiser 2", "Advertiser 2",  null,        new BigDecimal("3000"), new BigDecimal("30")});

        items.add(new Object[]{"Advertiser 3", "Campaign 4",  "BR",         new BigDecimal("4000"), new BigDecimal("40")});
        items.add(new Object[]{"Advertiser 3", "Campaign 4",  "Campaign 4", new BigDecimal("4000"), new BigDecimal("40")});

        items.add(new Object[]{"Advertiser 3", "Advertiser 3",  null,         new BigDecimal("4000"), new BigDecimal("40")});
        return items;
    }

    private static enum TestColumns implements DependentColumn {

        ADVERTISER(ColumnTypes.string(), NullAggregateFunction.instance()),
        CAMPAIGN(ColumnTypes.string(), NullAggregateFunction.instance()),
        COUNTRY(ColumnTypes.string(), NullAggregateFunction.instance()),
        VALUE(ColumnTypes.number(), SumAggregateFunction.factory()),
        VALUE2(ColumnTypes.number(), SumAggregateFunction.factory());

        private ColumnType columnType;
        private AggregateFunctionFactory functionFactory;


        TestColumns(ColumnType columnType, AggregateFunctionFactory functionFactory) {
            this.columnType = columnType;
            this.functionFactory = functionFactory;
        }

        @Override
        public ColumnType getType() {
            return columnType;
        }

        @Override
        public String getNameKey() {
            return this.name();
        }

        public static MetaData<TestColumns> metaData() {
            return new ReportMetaDataImpl("data", Arrays.asList(VALUE, VALUE2),
                    Arrays.asList(ADVERTISER, CAMPAIGN, COUNTRY), "context");
        }

        @Override
        public AggregateFunction newAggregateFunction() {
            return functionFactory.newInstance(this);
        }

        @Override
        public boolean hasAggregateFunction() {
            return true;
        }


        @Override
        public Set getDependentColumns(Object context) {
            return new HashSet<>();
        }
    }

    private static class TestColumnsRow implements Row<TestColumns> {
        private final Object[] item;

        public TestColumnsRow(Object[] item) {
            this.item = item;
        }

        @Override
        public Object get(TestColumns column) {
            return item[column.ordinal()];
        }

        @Override
        public RowType getType() {
            return RowTypes.data();
        }
    }

    private class TestHandler extends ResultSerializerSupport<TestHandler> {
        private List<Object[]> data = new ArrayList<Object[]>();

        public TestHandler() {
            super(null, new FormatterContext(null));
        }

        @Override
        public void row(Row row) {
            super.row(row);
            List<? extends Column> columns = metaData.getColumns();
            Object[] objects = new Object[columns.size()];

            for (int i = 0; i < metaData.getColumns().size(); i++) {
                Column column = columns.get(i);
                objects[i] = row.get(column);
            }

            data.add(objects);
        }

        public List<Object[]> getData() {
            return data;
        }
    }
}
