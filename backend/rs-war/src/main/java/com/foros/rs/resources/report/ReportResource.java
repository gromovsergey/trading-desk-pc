package com.foros.rs.resources.report;

import com.foros.reporting.tools.CancelQueryService;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapDisplayAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapGeneralAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapTextAdvertiserReportService;
import com.foros.session.reporting.conversions.ConversionsReportParameters;
import com.foros.session.reporting.conversions.ConversionsReportService;
import com.foros.session.reporting.referrer.ReferrerReportParameters;
import com.foros.session.reporting.referrer.ReferrerReportService;
import com.foros.util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Consumes(MediaType.APPLICATION_XML)
@Path("/reporting/")
public class ReportResource {

    @EJB
    private OlapGeneralAdvertiserReportService generalAdvertiserReportService;

    @EJB
    private OlapTextAdvertiserReportService textAdvertiserReportService;

    @EJB
    private OlapDisplayAdvertiserReportService displayAdvertiserReportService;

    @EJB
    private ReferrerReportService referrerReportService;

    @EJB
    private ConversionsReportService conversionsReportService;

    @EJB
    private CancelQueryService cancelQueryService;

    @Context
    private HttpServletResponse response;

    @POST
    @Path("/generalAdvertising")
    @Produces({"text/csv", "application/x-excel", MediaType.APPLICATION_XML})
    public Response generalAdvertising(@QueryParam("format") String format, OlapAdvertiserReportParameters parameters) throws IOException {
        return processAdvertisingReport(generalAdvertiserReportService, parameters, format);
    }

    @POST
    @Path("/textAdvertising")
    @Produces({"text/csv", "application/x-excel", MediaType.APPLICATION_XML})
    public Response textAdvertising(@QueryParam("format") String format, OlapAdvertiserReportParameters parameters) throws IOException {
        return processAdvertisingReport(textAdvertiserReportService, parameters, format);
    }

    @POST
    @Path("/displayAdvertising")
    @Produces({ "text/csv", "application/x-excel", MediaType.APPLICATION_XML })
    public Response displayAdvertising(@QueryParam("format") String format, OlapAdvertiserReportParameters parameters) throws IOException {
        return processAdvertisingReport(displayAdvertiserReportService, parameters, format);
    }

    private Response processAdvertisingReport(final OlapAdvertiserReportService service, final OlapAdvertiserReportParameters parameters, String format) throws IOException {
        // own account id is default for external users
        if (!SecurityContext.isInternal() && parameters.getAccountId() == null) {
            ApplicationPrincipal principal = SecurityContext.getPrincipal();
            parameters.setAccountId(principal.getAccountId());
        }

        process(fetchFormat(format).name(), new Work() {
            @Override
            public void processExcel(OutputStream os) {
                service.processExcel(parameters, os);
            }

            @Override
            public void processCsv(OutputStream os) {
                service.processCsv(parameters, os);
            }
        });

        return null;
    }

    @POST
    @Path("/referrer")
    @Produces({ "text/csv", "application/x-excel", MediaType.APPLICATION_XML })
    public Response referrer(
            final @QueryParam("format") String format,
            final ReferrerReportParameters parameters) throws IOException {

        if (!SecurityContext.isInternal() && parameters.getAccountId() == null) {
            ApplicationPrincipal principal = SecurityContext.getPrincipal();
            parameters.setAccountId(principal.getAccountId());
        }


        process(fetchFormat(format).name(), new Work() {
            @Override
            public void processExcel(OutputStream os) {
                referrerReportService.processExcel(parameters, os);
            }

            @Override
            public void processCsv(OutputStream os) {
                referrerReportService.processCsv(parameters, os);
            }
        });

        return null;
    }

    @POST
    @Path("/conversions")
    @Produces({ "text/csv", "application/x-excel", MediaType.APPLICATION_XML })
    public Response referrer(
            final @QueryParam("format") String format,
            final ConversionsReportParameters parameters) throws IOException {

        if (!SecurityContext.isInternal() && parameters.getAccountId() == null) {
            ApplicationPrincipal principal = SecurityContext.getPrincipal();
            parameters.setAccountId(principal.getAccountId());
        }

        process(fetchFormat(format).name(), new Work() {
            @Override
            public void processExcel(OutputStream os) {
                conversionsReportService.processExcel(parameters, os);
            }

            @Override
            public void processCsv(OutputStream os) {
                conversionsReportService.processCsv(parameters, os);
            }
        });

        return null;
    }


    private interface Work {
        void processExcel(OutputStream os);
        void processCsv(OutputStream os);
    }

    private void process(final String format, final Work work) {
        cancelQueryService.doCancellable(UUID.randomUUID().toString(), new Runnable() {
            @Override
            public void run() {
                ServletOutputStream os;
                try {
                    os = response.getOutputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
                switch (Format.find(format)) {
                    case excel:
                        work.processExcel(os);
                        break;
                    case csv:
                        work.processCsv(os);
                }
            }
        });
    }

    private Format fetchFormat(String format) {
        return fetchFormat(format, Format.csv);
    }

    private Format fetchFormat(String format, Format defaultFormat, Format... availableFormats) {
        Format realFormat = StringUtil.isPropertyEmpty(format) ? defaultFormat :  Format.find(format);
        List<Format> formats = availableFormats.length > 0 ? Arrays.asList(availableFormats) : Arrays.asList(Format.values());

        if (!formats.contains(realFormat)) {
            throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
        }

        return realFormat;
    }

    public enum Format {
        excel, csv;

        public static Format find(String name) {
            return Format.valueOf(name.toLowerCase());
        }

    }
}
