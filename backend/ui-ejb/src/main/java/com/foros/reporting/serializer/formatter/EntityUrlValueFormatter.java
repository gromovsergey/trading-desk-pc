package com.foros.reporting.serializer.formatter;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.session.ServiceLocator;
import com.foros.util.UrlUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;

public class EntityUrlValueFormatter extends ValueFormatterSupport<String> {

    protected final Column idColumn;
    protected final String urlPattern;
    protected final String baseUrl;

    protected EntityUrlValueFormatter(Column idColumn, String urlPattern) {
        this(idColumn, urlPattern, null);
    }

    protected EntityUrlValueFormatter(Column idColumn, String urlPattern, String baseUrl) {
        this.idColumn = idColumn;
        this.urlPattern = urlPattern;
        this.baseUrl = baseUrl;
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, String value, FormatterContext context) {
        if (baseUrl != null && value != null) {
            String text = formatText(value, context);
            Object columnValue = context.getRow().get(idColumn);
            if (columnValue != null && NumberUtils.isNumber(columnValue.toString())) {
                String excelUrl = UrlUtil.concat(baseUrl, "/excel", getUrl(context));
                cellAccessor.setLink(excelUrl, text);
                cellAccessor.addStyle(Styles.hyperlink());
            } else {
                super.formatExcel(cellAccessor, value, context);
            }
        } else {
            super.formatExcel(cellAccessor, value, context);
        }
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
        if (value != null) {
            String text = formatText(value, context);
            String innerHtml = StringEscapeUtils.escapeHtml(text);
            Object columnValue = context.getRow().get(idColumn);
            if (columnValue != null && NumberUtils.isNumber(columnValue.toString())) {
                cellAccessor.setHtml("<a href='" + getUrl(context) + "' target=\"_blank\">" + innerHtml + "</a>");
            } else {
                cellAccessor.setHtml(innerHtml);
            }
        } else {
            cellAccessor.setHtml("");
        }
    }

    @Override
    public String formatText(String value, FormatterContext context) {
        return value;
    }

    private String getUrl(FormatterContext context) {
        return String.format(urlPattern, Long.valueOf(context.getRow().get(idColumn).toString()));
    }

    public static EntityUrlValueFormatter html(Column idColumn, String urlPattern) {
        return new EntityUrlValueFormatter(idColumn, urlPattern);
    }

    public static class Factory {
        private final String baseUrl;

        public Factory() {
            baseUrl = ServiceLocator.getInstance().lookup(ConfigService.class).get(ConfigParameters.BASE_URL);
        }

        public EntityUrlValueFormatter all(Column idColumn, String urlPattern) {
            return new EntityUrlValueFormatter(idColumn, urlPattern, baseUrl);
        }

        public EntityUrlValueFormatter html(Column idColumn, String urlPattern) {
            return EntityUrlValueFormatter.html(idColumn, urlPattern);
        }
    }
}
