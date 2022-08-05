package com.foros.reporting.meta;

import com.foros.session.reporting.parameters.Order;

public interface ColumnType {

    String getName();

    Order getDefaultOrder();
}
