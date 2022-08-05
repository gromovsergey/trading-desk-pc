package com.foros.reporting.tools.query.cache;

import com.foros.reporting.RowType;

class RowStorage {

    private RowType rowType;
    private Object[] values;

    RowStorage(RowType rowType, Object[] values) {
        this.rowType = rowType;
        this.values = values;
    }

    public RowType getRowType() {
        return rowType;
    }

    public Object get(int index) {
        return values[index];
    }

}
