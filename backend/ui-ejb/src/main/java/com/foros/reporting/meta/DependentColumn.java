package com.foros.reporting.meta;

import java.util.Set;

public interface DependentColumn<C extends Column> extends AggregatableColumn {
    Set<C> getDependentColumns(Object context);
}
