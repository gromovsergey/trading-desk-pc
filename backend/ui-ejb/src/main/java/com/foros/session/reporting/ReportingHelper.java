package com.foros.session.reporting;

import com.foros.reporting.meta.DependentColumn;
import com.foros.reporting.meta.ResolvableMetaData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportingHelper {

    public static <C extends DependentColumn<C>> List<String> getColumnsSortPattern(ResolvableMetaData<C> resolvableMetaData) {
        return getColumnNamesList(resolvableMetaData.getColumns(), true);
    }

    public static <C extends DependentColumn<C>> List<String> getColumnNamesList(Collection<C> columns) {
        return getColumnNamesList(columns, false);
    }

    public static <C extends DependentColumn<C>> List<String> getColumnNamesList(Collection<C> columns, boolean quote) {
        List<String> nameKeys = new ArrayList<String>(columns.size());
        for (DependentColumn<?> col : columns) {
            if (quote) {
                nameKeys.add("'" + col.getNameKey() + "'");
            } else {
                nameKeys.add(col.getNameKey());
            }
        }
        return nameKeys;
    }
}
