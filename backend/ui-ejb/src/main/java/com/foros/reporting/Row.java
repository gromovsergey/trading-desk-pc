package com.foros.reporting;

import com.foros.reporting.meta.Column;

public interface Row<C extends Column> {

    Object get(C column);

    RowType getType();

}
