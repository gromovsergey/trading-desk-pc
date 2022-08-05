package com.foros.reporting;

import com.foros.reporting.meta.Column;

public class RowTypes {

    private static final RowType none = new RowType("none");
    private static final RowType data = new RowType("data");
    private static final RowType header = new RowType("header");
    private static final RowType total = new RowType("total");
    private static final RowType summary = new RowType("summary");

    public static RowType none() {
        return none;
    }

    public static RowType data() {
        return data;
    }

    public static RowType header() {
        return header;
    }

    public static RowType subTotal(Column column) {
        return new RowType("subtotal(" + column.getNameKey() + ")");
    }

    public static RowType total() {
        return total;
    }

    public static RowType summary() {
        return summary;
    }
}
