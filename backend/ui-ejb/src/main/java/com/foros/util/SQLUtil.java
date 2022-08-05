package com.foros.util;

import com.foros.session.cache.CacheServiceBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.jdbc.support.JdbcAccessor;

public class SQLUtil {
    private static final Logger logger = Logger.getLogger(CacheServiceBean.class.getName());

    public static final DateTimeFormatter PG_DATE_TIME_PARSER = createDateTimeParser();

    public static String quote(String src) {
        if (src == null) {
            return null;
        }
        return "'" + escapeApostrophe(src) + "'" ;
    }

    private static String escapeApostrophe(String src) {
        return src.replace("'", "''");
    }

    public static String getEscapedString(String src, char escape) {
        if (escape != '\\') {
            String escString = Character.toString(escape);
            String res = src.replaceAll(escString, escString + escString);
            res = res.replaceAll("%", escString + "%");
            res = res.replaceAll("_", escString + "_");
            return res;
        } else {
            String res = src.replaceAll("\\\\", "\\\\\\\\");
            res = res.replaceAll("%", "\\\\%");
            res = res.replaceAll("_", "\\\\_");
            return res;
        }
    }

    public static String getLikeEscape(String src) {
        if (StringUtil.isPropertyEmpty(src)) {
            return null;
        }
        return "%" + getEscapedString(src.toUpperCase(), '\\') + "%";
    }

    public static String formatINClause(String fieldName, Collection<? extends Number> elements) {
        return formatINClause(fieldName, elements, false);
    }

    private static <E> String formatINClause(String fieldName, Collection<E> elements, boolean appendQuotes) {
        if (elements == null || elements.isEmpty()) {
            return "";
        }

        final String quoteValue = appendQuotes ? "'": "";
        StringBuilder res = new StringBuilder();
        res.append('(').append(fieldName).append(" in (");

        boolean firstElement = true;
        for (E element : elements) {
            if (!firstElement) {
                res.append(',');
            }
            res.append(quoteValue);
            res.append(escapeApostrophe(String.valueOf(element)));
            res.append(quoteValue);
            firstElement = false;
        }

        res.append("))");

        return res.toString();
    }

    public static String escapeStructValue(String str) {
        if (str == null) {
            return "";
        }

        str = str.replaceAll("\\\\", "\\\\\\\\");
        str = str.replaceAll("\\\"", "\\\\\\\"");
        str = "\"".concat(str).concat("\"");
        return str;
    }

    public static String escapeStructValue(Character c) {
        if (c == null) {
            return "";
        }
        return escapeStructValue(c.toString());
    }

    public static <T> T nullSafeGet(ResultSet rs, String index, Class<T> type) throws SQLException {
        // Not implemented in postgres JDBC
        // T result = rs.getObject(index, type);
        Object result;
        if (Long.class.equals(type)) {
            result = rs.getLong(index);
        } else if (Integer.class.equals(type)) {
            result = rs.getInt(index);
        } else {
            throw new NotImplementedException();
        }

        if (rs.wasNull()) {
            return null;
        }
        return type.cast(result);
    }

    public static DateTimeFormatter getDateTimeParser() {
        return PG_DATE_TIME_PARSER;
    }

    private static DateTimeFormatter createDateTimeParser() {
        DateTimeParser timeOrOffset = new DateTimeFormatterBuilder()
                .appendLiteral(' ')
                .append(ISODateTimeFormat.timeElementParser().getParser())
                .toParser();
        return new DateTimeFormatterBuilder()
                .append(ISODateTimeFormat.dateElementParser())
                .appendOptional(timeOrOffset)
                .toFormatter();
    }

    /**
     * To eager initialize exceptionTranslator. See also OUI-28491.
     */
    public static void tryInitExceptionTranslator(JdbcAccessor jdbcAccessor) {
        try {
            jdbcAccessor.getExceptionTranslator();
            logger.log(Level.INFO, "{0}.exceptionTranslator is initialized", jdbcAccessor.getClass().getSimpleName());
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Failed to initialize exceptionTranslator", e);
        }
    }
}
