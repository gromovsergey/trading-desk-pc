package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.xlsx.Styles;

public abstract class HeaderFormatterSupport<T extends Column> extends ValueFormatterSupport<T> {

    @Override
    protected void setStyleId(ExcelCellAccessor cellAccessor, T value, FormatterContext context) {
        cellAccessor.addStyle(Styles.header());
    }
}
