package com.foros.reporting.serializer;

import com.foros.reporting.RowType;
import com.foros.reporting.meta.MetaData;
import com.foros.session.reporting.PreparedParameter;

import java.util.ArrayList;
import java.util.List;

public class SimpleReportData implements ReportData {
    private MetaData metaData;
    private HtmlCell[] headers;
    private SummaryData summary;
    private List<Row> rows = new ArrayList<Row>();
    private List<PreparedParameter> preparedParameters;
    private boolean partialResult = false;

    public void addRow(RowType rowType, HtmlCell[] row) {
        this.rows.add(new Row(rowType, row));
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public List<Row> getRows() {
        return rows;
    }

    public HtmlCell[] getHeaders() {
        return headers;
    }

    public void setHeaders(HtmlCell[] headers) {
        this.headers = headers;
    }

    @Override
    public List<PreparedParameter> getPreparedParameters() {
        return preparedParameters;
    }

    public void setPreparedParameters(List<PreparedParameter> preparedParameters) {
        this.preparedParameters = preparedParameters;
    }

    public SummaryData getSummary() {
        return summary;
    }

    public void setSummary(SummaryData summary) {
        this.summary = summary;
    }

    @Override
    public boolean isPartialResult() {
        return partialResult;
    }

    public void setPartialResult(boolean partialResult) {
        this.partialResult = partialResult;
    }

    public static class Row {
        private RowType rowType;
        private HtmlCell[] values;

        public Row(RowType rowType, HtmlCell[] values) {
            this.rowType = rowType;
            this.values = values;
        }

        public HtmlCell[] getValues() {
            return values;
        }

        public RowType getRowType() {
            return rowType;
        }
    }
}
