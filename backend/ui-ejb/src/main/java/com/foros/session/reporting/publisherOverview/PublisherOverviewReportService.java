package com.foros.session.reporting.publisherOverview;

import com.foros.model.account.Account;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.HashedValueFormatter;
import com.foros.reporting.serializer.formatter.HashedValueFormatter.ALGORITHM;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.sql.Types;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless(name = "PublisherOverviewReportService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class PublisherOverviewReportService implements GenericReportService<PublisherOverviewReportParameters, SimpleReportData> {

    private static final String SALT = "b76e8c89e8ef52f0fe8fb47df32aa8ba";
    private static final String PUBLISHER_URL_PATTERN = "../../../publisher/account/view.action?id=%d";

    private ValueFormatterRegistryImpl REGISTRY = ValueFormatterRegistries.registry()
        .column(PublisherOverviewReportMeta.PUBLISHER_NAME, EntityUrlValueFormatter.html(PublisherOverviewReportMeta.PUBLISHER_ID, PUBLISHER_URL_PATTERN))
        .column(PublisherOverviewReportMeta.PUBLISHER_ID, new HashedValueFormatter(ALGORITHM.MD5, SALT));
    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    protected StatsDbQueryProvider statsDb;

    @EJB
    private ReportsService reportsService;

    @EJB
    protected CurrentUserService currentUserService;

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.publisherOverviewReport", parameters = "#parameters")
    public void processHtml(PublisherOverviewReportParameters parameters, SimpleReportData data) {
        processReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.publisherOverviewReport", parameters = "#parameters")
    public void processExcel(PublisherOverviewReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.publisherOverviewReport", parameters = "#parameters")
    public void processCsv(PublisherOverviewReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processReport(PublisherOverviewReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer));
    }

    private class Report extends CommonAuditableReportSupport<PublisherOverviewReportParameters> {
        public Report(PublisherOverviewReportParameters parameters, AuditResultHandlerWrapper handler) {
            super(parameters, handler, true);
        }

        @Override
        public void prepare() {
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    Account account = em.find(Account.class, parameters.getAccountId());
                    builder.addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone());
                    builder.addId("account", Account.class, parameters.getAccountId());
                    builder.addYesNo("segmentByVertical", parameters.isSegmentByVertical());
                    builder.addYesNo("segmentByProduct", parameters.isSegmentByProduct());
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            handler.registry(REGISTRY, RowTypes.data());

            summaryMetaData = PublisherOverviewReportMeta.PUBLISHER_OVERVIEW_REPORT_SUMMARY.resolve(parameters);
            summaryQuery = statsDb.queryFunction("report.publisher_overview_summary")
                .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                .parameter("p_isp_id ", parameters.getAccountId(), Types.INTEGER);

            prepareMetadata();

            query = statsDb.queryFunction("report.publisher_overview")
                .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                .parameter("p_isp_id ", parameters.getAccountId(), Types.INTEGER)
                .parameter("p_by_vertical", parameters.isSegmentByVertical())
                .parameter("p_by_product", parameters.isSegmentByProduct());

        }

        private void prepareMetadata() {
            metaData = PublisherOverviewReportMeta.PUBLISHER_OVERVIEW_REPORT.resolve(parameters);
            if (currentUserService.isExternal()) {
                metaData = metaData.exclude(PublisherOverviewReportMeta.PUBLISHER_NAME);
            }

            if (!parameters.isSegmentByProduct()) {
                metaData = metaData.exclude(PublisherOverviewReportMeta.PRODUCT);
            }

            if (!parameters.isSegmentByVertical()) {
                metaData = metaData.exclude(PublisherOverviewReportMeta.VERTICAL);
            }
        }

        @Override
        public ReportType getReportType() {
            return ReportType.PUBLISHER_OVERVIEW;
        }
    }
}
