package com.foros.session.reporting.activeAdvertisers;

import com.foros.model.account.Account;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
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
@Stateless(name = "ActiveAdvertisersReportService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class ActiveAdvertisersReportService implements GenericReportService<ActiveAdvertisersReportParameters, SimpleReportData> {

    private static final String ADVERTISER_URL_PATTERN = "../../../account/view.action?id=%d";

    private static final ValueFormatterRegistryImpl REGISTRY = ValueFormatterRegistries.registry()
        .column(ActiveAdvertisersReportMeta.ADVERTISER_NAME, EntityUrlValueFormatter.html(ActiveAdvertisersReportMeta.ADVERTISER_ID, ADVERTISER_URL_PATTERN));

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
    @Validate(validation = "Reporting.activeAdvertisersReport", parameters = "#parameters")
    public void processHtml(ActiveAdvertisersReportParameters parameters, SimpleReportData data) {
        processReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.activeAdvertisersReport", parameters = "#parameters")
    public void processExcel(ActiveAdvertisersReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.activeAdvertisersReport", parameters = "#parameters")
    public void processCsv(ActiveAdvertisersReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processReport(ActiveAdvertisersReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer));
    }

    private class Report extends CommonAuditableReportSupport<ActiveAdvertisersReportParameters> {
        public Report(ActiveAdvertisersReportParameters parameters, AuditResultHandlerWrapper handler) {
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
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            handler.registry(currentUserService.isExternal() ? ValueFormatterRegistries.registry() : REGISTRY, RowTypes.data());

            summaryMetaData = ActiveAdvertisersReportMeta.ACTIVE_ADVERTISERS_REPORT_SUMMARY.resolve(parameters);
            summaryQuery = statsDb.queryFunction("report.active_advertisers_summary")
                .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                .parameter("p_isp_id ", parameters.getAccountId(), Types.INTEGER);

            metaData = ActiveAdvertisersReportMeta.ACTIVE_ADVERTISERS_REPORT.resolve(parameters);
            query = statsDb.queryFunction("report.active_advertisers")
                .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                .parameter("p_isp_id ", parameters.getAccountId(), Types.INTEGER);

        }

        @Override
        public ReportType getReportType() {
            return ReportType.ACTIVE_ADVERTISERS;
        }
    }
}
