package com.foros.reporting.meta;

import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunctionFactory;
import com.foros.reporting.tools.subtotal.aggreagate.NullAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.PercentAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.RatioAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.SumAggregateFunction;
import com.foros.util.CollectionUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetaDataBuilder {

    private String id;
    private DbColumn[] outputColumns = new DbColumn[0];
    private DbColumn[] metricsColumns = new DbColumn[0];

    public MetaDataBuilder(String id) {
        this.id = id;
    }

    public static MetaDataBuilder metaData(String id) {
        return new MetaDataBuilder("reports." + id);
    }

    public MetaDataBuilder outputColumns(DbColumn... outputColumns) {
        this.outputColumns = outputColumns;
        return this;
    }

    public MetaDataBuilder metricsColumns(DbColumn... metricsColumns) {
        this.metricsColumns = metricsColumns;
        return this;
    }

    public ResolvableMetaData<DbColumn> build() {
        return new ResolvableMetaDataImpl<>(id, Arrays.asList(metricsColumns), Arrays.asList(outputColumns));
    }

    public static DbColumn buildColumn(String id, String columnName, ColumnType type, DbColumn... dependency) {
        return column(id, columnName, type, dependency).build();
    }

    public static DbColumnBuilder column(String id, String columnName, ColumnType type, DbColumn... dependency) {
        return new DbColumnBuilder(
                "report.output.field." + id,
                columnName,
                type,
                dependency
        );
    }

    private static Map<Integer, ColumnType> sqlTypes = CollectionUtils
            .map(Types.NUMERIC, ColumnTypes.number())
            .map(Types.VARCHAR, ColumnTypes.string())
            .map(Types.DATE, ColumnTypes.dateTime())
            .build();

    public static ReportMetaData<DbColumn> metaData(ResultSetMetaData resultSetMetaData) throws SQLException {

//        ArrayList<DbColumn> dbColumns = new ArrayList<DbColumn>();
//        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
//            String columnLabel = resultSetMetaData.getColumnLabel(i);
//            String columnName = resultSetMetaData.getColumnName(i);
//            int columnType = resultSetMetaData.getColumnType(i);
//            ColumnType type = sqlTypes.get(columnType);
//            dbColumns.add(new DbColumn(columnLabel, columnName, type, aggregateFunction));
//        }

        return null; // TODO: new MetaData<DbColumn>(resultSetMetaData.getTableName(1), dbColumns);
    }

    public static Set<String> resultSetNames(Collection<DbColumn> columns) {
        HashSet<String> names = new HashSet<String>();
        for (DbColumn column : columns) {
            names.add(column.getResultSetName());
        }
        return names;
    }

    public static Set<String> resultSetNames(DbColumn... columns) {
        return resultSetNames(Arrays.asList(columns));
    }

    public static class DbColumnBuilder {
        private String nameKey;

        private ColumnType type;
        private String resultSetName;

        private DependenciesColumnResolver<DbColumn> dependenciesColumnResolver = new SimpleDependenciesColumnResolver<>();
        private AggregateFunctionFactory<DbColumn> aggregateFunctionFactory = NullAggregateFunction.instance();

        public DbColumnBuilder(
                String nameKey,
                String resultSetName,
                ColumnType type,
                DbColumn... dependencies) {
            this.nameKey = nameKey;
            this.type = type;
            this.resultSetName = resultSetName;
            this.dependenciesColumnResolver = new SimpleDependenciesColumnResolver<>(dependencies);
        }

        public DbColumn build() {
            return new DbColumn(nameKey, resultSetName, type, aggregateFunctionFactory, dependenciesColumnResolver);
        }

        public DbColumnBuilder aggregateSum() {
            aggregateFunctionFactory = SumAggregateFunction.factory();
            return this;
        }

        public DbColumnBuilder aggregateRatio(final DbColumn dividend, final DbColumn divisor) {
            return aggregate(new AggregateFunctionFactory<DbColumn>() {
                @Override
                public AggregateFunction newInstance(Column column) {
                    return new RatioAggregateFunction(column, dividend.newAggregateFunction(), divisor.newAggregateFunction());
                }

                @Override
                public Collection<DbColumn> getDependedColumns() {
                    return Arrays.asList(dividend, divisor);
                }
            });
        }

        public DbColumnBuilder aggregatePercent(final DbColumn dividend, final DbColumn divisor) {
            return aggregate(new AggregateFunctionFactory<DbColumn>() {
                @Override
                public AggregateFunction newInstance(Column column) {
                    return new PercentAggregateFunction(column, dividend.newAggregateFunction(), divisor.newAggregateFunction());
                }

                @Override
                public Collection<DbColumn> getDependedColumns() {
                    return Arrays.asList(dividend, divisor);
                }
            });
        }

        public DbColumnBuilder aggregate(final AggregateFunctionFactory<DbColumn> aggregateFunctionFactory) {
            this.aggregateFunctionFactory = aggregateFunctionFactory;
            return this;
        }
    }
}
