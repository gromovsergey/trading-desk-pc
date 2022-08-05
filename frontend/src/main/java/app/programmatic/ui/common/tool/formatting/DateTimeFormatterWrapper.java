package app.programmatic.ui.common.tool.formatting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterWrapper {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String FORMAT = DATE_FORMAT + " HH:mm";
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMAT);

    public static String format(LocalDateTime src) {
        return src == null ? null : FORMATTER.format(src);
    }

    public static LocalDateTime parseDateTime(String src) {
        return (src == null || src.trim().isEmpty()) ? null : LocalDateTime.parse(src, FORMATTER);
    }

    public static String format(LocalDate src) {
        return src == null ? null : DATE_FORMATTER.format(src);
    }

    public static LocalDate parseDate(String src) {
        if (src == null || src.trim().isEmpty()) {
            return null;
        }
        return (src.length() > DATE_FORMAT.length()) ? LocalDate.parse(src, FORMATTER) : LocalDate.parse(src, DATE_FORMATTER);
    }
}
