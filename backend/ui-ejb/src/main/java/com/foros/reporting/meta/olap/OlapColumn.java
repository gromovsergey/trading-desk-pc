package com.foros.reporting.meta.olap;

import com.phorm.oix.olap.OlapIdentifier;
import com.foros.reporting.meta.AbstractDependentColumn;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.DependenciesColumnResolver;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunctionFactory;

public class OlapColumn extends AbstractDependentColumn<OlapColumn> {

    protected final OlapColumnType olapColumnType;
    protected final MemberResolver resolver;

    public OlapColumn(
            String nameKey,
            OlapColumnType olapColumnType,
            ColumnType columnType,
            MemberResolver resolver,
            AggregateFunctionFactory<OlapColumn> aggregateFunctionFactory,
            DependenciesColumnResolver<OlapColumn> dependenciesColumnResolver) {
        super(nameKey, columnType, aggregateFunctionFactory, dependenciesColumnResolver);
        this.olapColumnType = olapColumnType;
        this.resolver = resolver;
    }

    public OlapColumnType getOlapColumnType() {
        return olapColumnType;
    }

    public OlapIdentifier getMember(Object context) {
        return resolver.resolve(context);
    }

    @Override
    public String toString() {
        return "OlapColumn{" + olapColumnType + ":" + getNameKey() + '}';
    }
}
