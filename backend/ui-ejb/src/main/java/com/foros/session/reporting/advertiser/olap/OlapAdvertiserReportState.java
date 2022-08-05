package com.foros.session.reporting.advertiser.olap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.meta.olap.OlapColumn;

public class OlapAdvertiserReportState {
    private ReportMetaData<OlapColumn> available;
    private ReportMetaData<OlapColumn> selected;
    private Set<OlapColumn> fixedColumns;
    private List<Set<OlapColumn>> subtotalLevels;

    public OlapAdvertiserReportState(
            ReportMetaData<OlapColumn> available,
            ReportMetaData<OlapColumn> selected,
            Set<OlapColumn> fixedColumns,
            List<Set<OlapColumn>> subtotalLevels) {
        this.available = available;
        this.selected = selected;
        this.fixedColumns = Collections.unmodifiableSet(fixedColumns);
        this.subtotalLevels = Collections.unmodifiableList(subtotalLevels);
    }

    public boolean available(String column) {
        if (available.contains(column)) {
            if (!fixedColumns.contains(available.find(column))) {
                return true;
            }
        }
        return false;
    }

    public boolean availableOrFixed(String column) {
        return available.contains(column);
    }

    public boolean selected(String column) {
        if (!available.contains(column)) {
            // unavailable -> not selected
            return false;
        }

        if (selected.contains(column)) {
            return true;
        }

        // for net\gross columns need to check other columns
        OlapColumn dbColumn = available.find(column);
        OlapAdvertiserMeta.NetGrossPair pair = OlapAdvertiserMeta.NET_GROSS_TRIPLETS.get(dbColumn);
        if (pair != null &&
            (selected.getColumns().contains(pair.getNet()) || selected.getColumns().contains(pair.getGross()))) {
            return true;
        }

        List<OlapColumn> wsCols = OlapAdvertiserMeta.WG_TRIPLETS.get(dbColumn);
        if (wsCols != null ) {
            for (OlapColumn wsCol : wsCols) {
                if (selected.contains(wsCol.getNameKey())) {
                    return true;
                }
            }
        }

        return false;
    }

    public ReportMetaData<OlapColumn> getAvailable() {
        return available;
    }

    public void setAvailable(ReportMetaData<OlapColumn> available) {
        this.available = available;
    }

    public ReportMetaData<OlapColumn> getSelected() {
        return selected;
    }

    public List<String> getSelectedColumnNames() {
        List<String> result = new ArrayList<String>();
        for (OlapColumn column : selected.getMetricsColumns()) {
            result.add(column.getNameKey());
        }
        for (OlapColumn column : selected.getOutputColumns()) {
            result.add(column.getNameKey());
        }
        return result;
    }

    public void setSelected(ReportMetaData<OlapColumn> selected) {
        this.selected = selected;
    }

    public Set<OlapColumn> getFixedColumns() {
        return fixedColumns;
    }

    public void setFixedColumns(Set<OlapColumn> fixedColumns) {
        this.fixedColumns = fixedColumns;
    }

    public List<Set<OlapColumn>> getSubtotalLevels() {
        return subtotalLevels;
    }

    public void setSubtotalLevels(List<Set<OlapColumn>> subtotalLevels) {
        this.subtotalLevels = subtotalLevels;
    }
}
