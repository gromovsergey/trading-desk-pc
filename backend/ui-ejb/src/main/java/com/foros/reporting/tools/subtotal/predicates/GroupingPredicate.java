package com.foros.reporting.tools.subtotal.predicates;

import com.foros.reporting.Row;

public interface GroupingPredicate {
    void checkRow(Row row);
    boolean isNewGroup();
}
