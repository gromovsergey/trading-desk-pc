package app.programmatic.ui.reporting.model;

import java.math.BigDecimal;

public enum ReportColumnType {
    DATE_COLUMN(null),
    TEXT_COLUMN(""),
    INT_COLUMN(Long.valueOf(0)),
    FLOAT_COLUMN(BigDecimal.ZERO),
    PERCENT_COLUMN(BigDecimal.ZERO),
    CURRENCY_COLUMN(BigDecimal.ZERO);

    private Object zeroValue;

    ReportColumnType(Object zeroValue) {
        this.zeroValue = zeroValue;
    }

    public Object getZeroValue() {
        return zeroValue;
    }
}
