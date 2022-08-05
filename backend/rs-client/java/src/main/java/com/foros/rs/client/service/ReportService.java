package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.data.JAXBEntity;
import com.foros.rs.client.data.OutputStreamResponseHandler;
import com.foros.rs.client.model.report.ReportFormat;
import com.foros.rs.client.model.report.conversions.ConversionsReportParameters;
import com.foros.rs.client.model.report.referrer.ReferrerReportParameters;
import com.foros.rs.client.model.report.advertiser.AdvertiserReportParameters;
import com.foros.rs.client.util.UrlBuilder;

import java.io.OutputStream;

public class ReportService {

    private final RsClient rsClient;

    public ReportService(RsClient rsClient) {
        this.rsClient = rsClient;
    }

    public void processGeneralAdvertisingReport(AdvertiserReportParameters reportParameters, ReportFormat format, OutputStream stream) {
        process("/reporting/generalAdvertising", reportParameters, format, stream);
    }

    public void processTextAdvertisingReport(AdvertiserReportParameters reportParameters, ReportFormat format, OutputStream stream) {
        process("/reporting/textAdvertising", reportParameters, format, stream);
    }

    public void processDisplayAdvertisingReport(AdvertiserReportParameters reportParameters, ReportFormat format, OutputStream stream) {
        process("/reporting/displayAdvertising", reportParameters, format, stream);
    }

    public void processReferrerReport(ReferrerReportParameters reportParameters, ReportFormat format, OutputStream stream) {
        process("/reporting/referrer", reportParameters, format, stream);
    }

    public void processConversionsReport(ConversionsReportParameters reportParameters, ReportFormat format, OutputStream stream) {
        process("/reporting/conversions", reportParameters, format, stream);
    }

    private void process(String path, Object reportParameters, ReportFormat format, OutputStream stream) {
        String url = UrlBuilder.path(path)
                .setQueryParameter("format", format.name())
                .toString();

        rsClient.post(url, new JAXBEntity(reportParameters), new OutputStreamResponseHandler(stream));
    }

}
