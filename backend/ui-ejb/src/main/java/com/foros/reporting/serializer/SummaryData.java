package com.foros.reporting.serializer;

import com.foros.reporting.meta.MetaData;

public class SummaryData {
    private MetaData metaData;
    private HtmlCell[] values;
    private HtmlCell[] headers;

    public SummaryData() {
    }

    public SummaryData(MetaData metaData, HtmlCell[] values) {
        this.metaData = metaData;
        this.values = values;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public HtmlCell[] getValues() {
        return values;
    }

    public void setValues(HtmlCell[] values) {
        this.values = values;
    }

    public HtmlCell[] getHeaders() {
        return headers;
    }

    public void setHeaders(HtmlCell[] headers) {
        this.headers = headers;
    }
}
