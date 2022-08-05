package app.programmatic.ui.common.tool.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeConverter {
    public static LocalDateTime toStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    public static LocalDateTime toEndOfDay(LocalDate date) {
        // Old UI expects seconds = 00
        return date == null ? null : date.atTime(23, 59,0);
    }
}
