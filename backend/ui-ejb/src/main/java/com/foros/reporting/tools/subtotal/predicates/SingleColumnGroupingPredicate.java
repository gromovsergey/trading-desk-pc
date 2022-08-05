package com.foros.reporting.tools.subtotal.predicates;

import com.foros.reporting.Row;
import com.foros.reporting.meta.Column;

import org.apache.commons.lang.ObjectUtils;

public class SingleColumnGroupingPredicate implements GroupingPredicate {

    private boolean beforeFirstRow = true;
    private GroupingPredicate parent;

    private Object value;
    private Column column;
    private boolean isNewGroup = false;

    public SingleColumnGroupingPredicate(Column column, GroupingPredicate parent) {
        this.column = column;
        this.parent = parent;
    }

    @Override
    public void checkRow(Row row) {
        Object newValue = fetchValue(row);
        if (!beforeFirstRow) {
            isNewGroup = !ObjectUtils.equals(value, newValue);
        }
        value = newValue;
        this.beforeFirstRow = false;
    }

    private Object fetchValue(Row row) {
        return row.get(column);
    }

    public boolean isNewGroup() {
        return parent.isNewGroup() || isNewGroup;
    }
}
