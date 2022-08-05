package com.foros.session.reporting.channeltriggers;

import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.ReportData;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.PreparedParameter;

import java.util.ArrayList;
import java.util.List;

public class ChannelTriggerReportData implements ReportData {

    private List<SimpleReportData> dataList = new ArrayList<SimpleReportData>(4);
    private List<PreparedParameter> preparedParameters;
    private boolean partialResult = false;

    public void add(MetaData metaData, SimpleReportData data) {
        data.setMetaData(metaData);
        dataList.add(data);
    }

    public void setPreparedParameters(List<PreparedParameter> preparedParameters) {
        this.preparedParameters = preparedParameters;
    }

    @Override
    public List<PreparedParameter> getPreparedParameters() {
        return preparedParameters;
    }

    @Override
    public boolean isPartialResult() {
        return partialResult;
    }

    public void setPartialResult(boolean partialResult) {
        this.partialResult = partialResult;
    }

    public SimpleReportData getUrls() {
        return getData(0);
    }

    public SimpleReportData getPageKeywords() {
        return getData(1);
    }

    public SimpleReportData getSearchKeywords() {
        return getData(2);
    }

    public SimpleReportData getUrlKeywords() {
        return getData(3);
    }

    private SimpleReportData getData(int i) {
        if (i >= dataList.size()) {
            return null;
    }

        return dataList.get(i);
    }
}
