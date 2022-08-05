package app.programmatic.ui.reporting.model;

import java.util.List;

public class ReportRows {
    private String[] header;
    private List<ColumnValue[]> rows;
    private ColumnValue[] totalRow;

    public ReportRows(String[] header, List<ColumnValue[]> rows, ColumnValue[] totalRow) {
        this.header = header;
        this.rows = rows;
        this.totalRow = totalRow;
    }

    public ReportRows(String[] header, List<ColumnValue[]> rows) {
        this(header, rows, null);
    }

    public String[] getHeader() {
        return header;
    }

    public List<ColumnValue[]> getRows() {
        return rows;
    }

    public ColumnValue[] getTotalRow() {
        return totalRow;
    }

    public static class ColumnValue {
        private Object value;
        private ReportColumn column;
        private boolean isZero;
        private String currencySign;

        public ColumnValue(Object value, ReportColumn column, boolean isZero) {
            this(value, column, isZero, null);
        }

        public ColumnValue(Object value, ReportColumn column, boolean isZero, String currencySign) {
            this.value = value;
            this.column = column;
            this.isZero = isZero;
            this.currencySign = currencySign;
        }

        public Object getValue() {
            return value;
        }

        public ReportColumn getColumn() {
            return column;
        }

        public boolean isZero() {
            return isZero;
        }

        public String getCurrencySign() {
            return currencySign;
        }
    }
}
