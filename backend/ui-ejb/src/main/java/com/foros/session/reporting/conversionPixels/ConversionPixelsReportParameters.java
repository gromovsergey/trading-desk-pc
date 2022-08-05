package com.foros.session.reporting.conversionPixels;

import com.foros.session.reporting.parameters.DatedReportParameters;

import java.util.ArrayList;
import java.util.List;

public class ConversionPixelsReportParameters extends DatedReportParameters {
    private Long accountId;
    private boolean showResultsByDay;
    private List<Long> conversionAdvertiserIds = new ArrayList<Long>();
    private List<Long> conversionIds = new ArrayList<Long>();

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public boolean isShowResultsByDay() {
        return showResultsByDay;
    }

    public void setShowResultsByDay(boolean showResultsByDay) {
        this.showResultsByDay = showResultsByDay;
    }

    public List<Long> getConversionIds() {
        return conversionIds;
    }

    public void setConversionIds(List<Long> conversionIds) {
        this.conversionIds = conversionIds;
    }

    public List<Long> getConversionAdvertiserIds() {
        return conversionAdvertiserIds;
    }

    public void setConversionAdvertiserIds(List<Long> advertiserIds) {
        this.conversionAdvertiserIds = advertiserIds;
    }
}
