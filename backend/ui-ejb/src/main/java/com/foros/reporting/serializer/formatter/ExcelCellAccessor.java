package com.foros.reporting.serializer.formatter;

import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public interface ExcelCellAccessor {
    void setString(String text);
    void setDouble(double v);
    void setDate(LocalDateTime dateTime);
    void setDate(LocalDate dateTime);
    void setLink(String href, String text);
    void setDate(Date date);

    void addStyle(String style);
}
