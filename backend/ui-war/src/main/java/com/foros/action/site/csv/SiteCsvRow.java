package com.foros.action.site.csv;

import com.foros.action.bulk.CsvRow;
import com.foros.reporting.RowType;
import com.foros.reporting.meta.Column;
import com.foros.util.csv.CsvField;

public class SiteCsvRow extends CsvRow {
    public static final RowType SITE = new RowType("site");
    public static final RowType SITE_TAG = new RowType("site_tag");

    private RowType rowType;
    private boolean isInternalMode;

    public SiteCsvRow(int rowSize, boolean isInternalMode) {
        super(rowSize);
        this.isInternalMode = isInternalMode;
    }

    @Override
    public RowType getType() {
        RowType type = super.getType();
        if (type != UNPARSED_ROW_TYPE) {
            type = rowType;
        }
        return type;
    }

    public void setRowType(RowType rowType) {
        this.rowType = rowType;
    }

    @Override
    public void set(CsvField field, Object value) {
        if (isInternalMode) {
            setInternal(field.getId(), value);
        } else {
            setInternal(field.getId() - SiteFieldCsv.SHIFT, value);
        }
    }

    @Override
    public Object get(Column column) {
        Enum field = (Enum) column;
        if (isInternalMode) {
            return getInternal(field.ordinal());
        } else {
            return getInternal(field.ordinal() - SiteFieldCsv.SHIFT);
        }
    }


}
