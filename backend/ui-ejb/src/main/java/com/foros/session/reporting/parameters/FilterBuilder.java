package com.foros.session.reporting.parameters;

import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FilterBuilder {
    private List<ReportFilterColsSqlType> filter = new ArrayList<ReportFilterColsSqlType>();

    public FilterBuilder isEquals(String columnName, Object value) {
        isEquals(columnName, value, false);
        return this;
    }

    public FilterBuilder isEquals(String columnName, Object value, boolean quote) {
        if (!isValueEmpty(value)) {
            String strValue = value.toString();
            if (quote) {
                strValue = SQLUtil.quote(strValue);
            }
            filter.add(new ReportFilterColsSqlType(columnName, "=", strValue));
        }
        return this;
    }

    public FilterBuilder isNull(String columnName) {
        filter.add(new ReportFilterColsSqlType(columnName, "is null", ""));
        return this;
    }

    private boolean isValueEmpty(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof String && StringUtil.isPropertyEmpty((String) value)) {
            return true;
        }

        return false;
    }

    public List<ReportFilterColsSqlType> getFilter() {
        return filter;
    }
}
