package com.foros.reporting.tools.subtotal.predicates;

import com.foros.reporting.Row;

public class TotalGroupingPredicate implements GroupingPredicate {
    @Override
    public void checkRow(Row row) {
    }

    @Override
    public boolean isNewGroup() {
        return false;
    }
}
