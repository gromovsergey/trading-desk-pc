package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.ExcelStyles;

public class ExcelFormatterContext extends FormatterContext {

    public ExcelFormatterContext(ExcelStyles styles) {
        super(styles.getLocale());
    }
}
