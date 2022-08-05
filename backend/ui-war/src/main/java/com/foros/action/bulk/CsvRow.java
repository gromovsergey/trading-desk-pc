package com.foros.action.bulk;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.Column;
import com.foros.util.csv.CsvField;

public class CsvRow implements Row {
    public static final RowType UNPARSED_ROW_TYPE = new RowType("unparsed");

    private Object[] data;
    private boolean unparsed = false;

    public CsvRow(int size) {
        this.data = new Object[size];
    }

    public void set(CsvField field, Object value) {
        setInternal(field.getId(), value);
    }

    protected void setInternal(int columnNumber, Object value) {
        data[columnNumber] = value;
    }

    @Override
    public Object get(Column column) {
        CsvField field = (CsvField) column;
        return getInternal(field.getId());
    }

    protected Object getInternal(int columnNumber) {
        return data[columnNumber];
    }

    @Override
    public RowType getType() {
        if (unparsed) {
            return UNPARSED_ROW_TYPE;
        } else {
            return RowTypes.data();
        }
    }

    public boolean isUnparsed() {
        return unparsed;
    }

    public void setUnparsed(boolean unparsed) {
        this.unparsed = unparsed;
    }
}
