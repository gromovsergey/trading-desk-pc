package com.foros.action.creative.csv;

import com.foros.action.bulk.CsvRow;
import com.foros.reporting.meta.Column;
import com.foros.util.csv.CsvField;

public class CreativeCsvRow extends CsvRow {
    private CsvField[] columnTypes;

    public CreativeCsvRow(int size) {
        super(size);
        this.columnTypes = new CsvField[size];
    }

    @Override
    public void set(CsvField field, Object value) {
        super.set(field, value);
        columnTypes[field.getId()] = field;
    }

    public Column getColumnWithRightType(Column column) {
        CsvField field = (CsvField) column;
        return columnTypes[field.getId()];
    }
}
