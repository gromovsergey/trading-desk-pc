package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.Row;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.DependentColumn;
import com.foros.util.NumberUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

public class OneCurrencyAggregateFunction implements AggregateFunction {

    private Column target;
    private Column currencyColumn;
    private BigDecimal sum = null;
    private String oneCurrency;
    private boolean multipleCurrency;

    public OneCurrencyAggregateFunction(Column target, Column currencyColumn) {
        this.target = target;
        this.currencyColumn = currencyColumn;
    }

    @Override
    public void aggregate(Row row) {
        if (multipleCurrency) {
            return;
        }

        String currency = (String) row.get(currencyColumn);
        if (oneCurrency == null) {
            oneCurrency = currency;
        } else if (!oneCurrency.equals(currency)){
            multipleCurrency = true;
            sum = null;
        }

        BigDecimal decimal = NumberUtil.toBigDecimal((Number) row.get(target));
        if (decimal != null) {

            if (sum == null ) {
                sum = BigDecimal.ZERO;
            }
            sum = sum.add(decimal);
        }
    }

    @Override
    public void clean() {
        sum = null;
    }

    @Override
    public Object getValue() {
        return multipleCurrency ? null : sum;
    }

    public static <C extends DependentColumn<C>> AggregateFunctionFactory<C> factory(final C currencyColumn) {
        return new AggregateFunctionFactory<C>() {
            @Override
            public AggregateFunction newInstance(Column column) {
                return new OneCurrencyAggregateFunction(column, currencyColumn);
            }

            @Override
            public Collection<C> getDependedColumns() {
                return Collections.emptyList();
            }
        };
    }
}
