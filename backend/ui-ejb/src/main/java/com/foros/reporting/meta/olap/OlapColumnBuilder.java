package com.foros.reporting.meta.olap;

import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.DependenciesColumnResolver;
import com.foros.reporting.meta.SimpleDependenciesColumnResolver;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunctionFactory;
import com.foros.reporting.tools.subtotal.aggreagate.NullAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.PercentAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.RatioAggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.SumAggregateFunction;

import java.util.Arrays;
import java.util.Collection;

public class OlapColumnBuilder {

    private String nameKey;

    private OlapColumnType olapColumnType;
    private ColumnType type;
    private MemberResolver resolver;
    private DependenciesColumnResolver<OlapColumn> dependenciesColumnResolver = new SimpleDependenciesColumnResolver<>();
    private AggregateFunctionFactory<OlapColumn> aggregateFunctionFactory = NullAggregateFunction.instance();

    public OlapColumnBuilder(String nameKey, OlapColumnType olapColumnType, ColumnType type, MemberResolver resolver) {
        this.nameKey = nameKey;
        this.olapColumnType = olapColumnType;
        this.type = type;
        this.resolver = resolver;
    }

    public OlapColumn build() {
        return new OlapColumn(nameKey, olapColumnType, type, resolver, aggregateFunctionFactory, dependenciesColumnResolver);
    }

    public OlapColumnBuilder dependencies(OlapColumn...dependencies) {
        this.dependenciesColumnResolver = new SimpleDependenciesColumnResolver<>(dependencies);
        return this;
    }

    public OlapColumnBuilder dependency(DependenciesColumnResolver<OlapColumn> dependenciesColumnResolver) {
        this.dependenciesColumnResolver = dependenciesColumnResolver;
        return this;
    }

    public OlapColumnBuilder dependency(OlapColumn dependency) {
        this.dependenciesColumnResolver = new SimpleDependenciesColumnResolver<>(dependency);
        return this;
    }

    public OlapColumnBuilder aggregateSum() {
        aggregateFunctionFactory = SumAggregateFunction.factory();
        return this;
    }

    public OlapColumnBuilder aggregateRatio(final OlapColumn dividend, final OlapColumn divisor) {
        return aggregate(new AggregateFunctionFactory<OlapColumn>() {
            @Override
            public AggregateFunction newInstance(Column column) {
                return new RatioAggregateFunction(column, dividend.newAggregateFunction(), divisor.newAggregateFunction());
            }

            @Override
            public Collection<OlapColumn> getDependedColumns() {
                return Arrays.asList(dividend, divisor);
            }
        });
    }

    public OlapColumnBuilder aggregatePercent(final OlapColumn dividend, final OlapColumn divisor) {
        return aggregate(new AggregateFunctionFactory<OlapColumn>() {
            @Override
            public AggregateFunction newInstance(Column column) {
                return new PercentAggregateFunction(column, dividend.newAggregateFunction(), divisor.newAggregateFunction());
            }

            @Override
            public Collection<OlapColumn> getDependedColumns() {
                return Arrays.asList(dividend, divisor);
            }
        });
    }

    public OlapColumnBuilder aggregate(final AggregateFunctionFactory<OlapColumn> aggregateFunctionFactory) {
        this.aggregateFunctionFactory = aggregateFunctionFactory;
        return this;
    }
}
