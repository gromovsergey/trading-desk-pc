package app.programmatic.ui.reporting;

import app.programmatic.ui.reporting.segments.SegmentsReportParameters;
import app.programmatic.ui.reporting.segments.SegmentsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.programmatic.ui.reporting.conversions.ConversionsReportParameters;
import app.programmatic.ui.reporting.conversions.ConversionsReportService;
import app.programmatic.ui.reporting.detailed.DetailedReportParameters;
import app.programmatic.ui.reporting.detailed.DetailedReportService;
import app.programmatic.ui.reporting.model.ReportFormat;
import app.programmatic.ui.reporting.advertiser.AdvertiserReportParameters;
import app.programmatic.ui.reporting.advertiser.AdvertiserReportService;
import app.programmatic.ui.reporting.domains.DomainsReportParameters;
import app.programmatic.ui.reporting.domains.DomainsReportService;
import app.programmatic.ui.reporting.publisher.PublisherReportParameters;
import app.programmatic.ui.reporting.publisher.PublisherReportService;
import app.programmatic.ui.reporting.referrer.ReferrerReportParameters;
import app.programmatic.ui.reporting.referrer.ReferrerReportService;
import app.programmatic.ui.reporting.view.ReportMeta;

@RestController
public class ReportController {

    @Autowired
    private AdvertiserReportService advertiserReportService;

    @Autowired
    private ConversionsReportService conversionsReportService;

    @Autowired
    private DomainsReportService domainsReportService;

    @Autowired
    private PublisherReportService publisherReportService;

    @Autowired
    private ReferrerReportService referrerReportService;

    @Autowired
    private DetailedReportService detailedReportService;

    @Autowired
    private SegmentsReportService segmentsReportService;

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/advertiser/meta", produces = "application/json")
    public ReportMeta getAdvertiserReportMeta(@RequestBody AdvertiserReportParameters parameters) {
        return advertiserReportService.getReportMeta(parameters);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/advertiser/run")
    public ResponseEntity runAdvertiserReport(@RequestParam(value = "format") ReportFormat format,
                                              @RequestBody AdvertiserReportParameters parameters) {
        byte[] reportData = advertiserReportService.runReportAdvertiser(parameters, format);
        return prepareResponse(reportData, format);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/conversions/meta", produces = "application/json")
    public ReportMeta getConversionsReportMeta(@RequestBody ConversionsReportParameters parameters) {
        return conversionsReportService.getReportMeta(parameters);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/conversions/run")
    public ResponseEntity runConversionsReport(@RequestParam(value = "format") ReportFormat format,
                                               @RequestBody ConversionsReportParameters parameters) {
        byte[] reportData = conversionsReportService.runReportConversions(parameters, format);
        return prepareResponse(reportData, format);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/domains/meta", produces = "application/json")
    public ReportMeta getDomainsReportMeta(@RequestBody DomainsReportParameters parameters) {
        return domainsReportService.getReportMeta(parameters);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/domains/run")
    public ResponseEntity runDomainsReport(@RequestParam(value = "format") ReportFormat format,
                                           @RequestBody DomainsReportParameters parameters) {
        byte[] reportData = domainsReportService.runReportDomains(parameters, format);
        return prepareResponse(reportData, format);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/publisher/meta", produces = "application/json")
    public ReportMeta getPublisherReportMeta(@RequestBody PublisherReportParameters parameters) {
        return publisherReportService.getReportMeta(parameters);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/publisher/run")
    public ResponseEntity runPublisherReport(@RequestParam(value = "format") ReportFormat format,
                                             @RequestBody PublisherReportParameters parameters) {
        byte[] reportData = publisherReportService.runReportNew(parameters, format);
        return prepareResponse(reportData, format);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/referrer/meta", produces = "application/json")
    public ReportMeta getReferrerReportMeta(@RequestBody ReferrerReportParameters parameters) {
        return referrerReportService.getReportMeta(parameters);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/referrer/run")
    public ResponseEntity runReferrerReport(@RequestParam(value = "format") ReportFormat format,
                                             @RequestBody ReferrerReportParameters parameters) {
        byte[] reportData = referrerReportService.runReportReferrer(parameters, format);
        return prepareResponse(reportData, format);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/detailed/meta", produces = "application/json")
    public ReportMeta getDetailedReportMeta(@RequestBody DetailedReportParameters parameters) {
        return detailedReportService.getReportMeta(parameters);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/detailed/run")
    public ResponseEntity runDetailedReport(@RequestParam(value = "format") ReportFormat format,
                                            @RequestBody DetailedReportParameters parameters) {
        byte[] reportData = detailedReportService.runReportDetailed(parameters, format);
        return prepareResponse(reportData, format);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/segments/meta", produces = "application/json")
    public ReportMeta getSegmentsReportMeta(@RequestBody SegmentsReportParameters parameters) {
        return segmentsReportService.getReportMeta(parameters);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/report/segments/run")
    public ResponseEntity runSegmentsReport(@RequestParam(value = "format") ReportFormat format,
                                            @RequestBody SegmentsReportParameters parameters) {
        byte[] reportData = segmentsReportService.runReportSegments(parameters, format);
        return prepareResponse(reportData, format);
    }

    private static ResponseEntity prepareResponse(byte[] reportData, ReportFormat format) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(format.getMediaType());
        if (format.isFileFormat()) {
            headers.setContentDispositionFormData("attachment", "report." + format.getFileExtension());
        }
        headers.setContentLength(reportData.length);
        return new ResponseEntity<>(
                reportData,
                headers,
                HttpStatus.OK);
    }
}
