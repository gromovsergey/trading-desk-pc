package com.foros.session.reporting.advertiser.olap;

import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.reporting.meta.olap.OlapColumn;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class OlapAdvertiserReportDescription {
    private ResolvableMetaData<OlapColumn> resolvableMetaData;
    private Set<OlapColumn> fixedColumns;
    private Set<OlapColumn> defaultColumns;
    private List<Set<OlapColumn>> subtotalLevels;

    public OlapAdvertiserReportDescription(
            ResolvableMetaData<OlapColumn> resolvableMetaData,
            Set<OlapColumn> fixedColumns,
            Set<OlapColumn> defaultColumns,
            List<Set<OlapColumn>> subtotalLevels) {
        this.resolvableMetaData = resolvableMetaData;
        this.fixedColumns = Collections.unmodifiableSet(fixedColumns);
        this.defaultColumns = Collections.unmodifiableSet(defaultColumns);
        this.subtotalLevels = Collections.unmodifiableList(subtotalLevels);
    }

    public ResolvableMetaData<OlapColumn> getResolvableMetaData() {
        return resolvableMetaData;
    }

    public Set<OlapColumn> getFixedColumns() {
        return fixedColumns;
    }

    public Set<OlapColumn> getDefaultColumns() {
        return defaultColumns;
    }

    public List<Set<OlapColumn>> getSubtotalLevels() {
        return subtotalLevels;
    }
}
