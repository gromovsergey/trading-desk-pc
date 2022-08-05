package com.foros.session.reporting.profiling;

import com.foros.model.account.Account;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.HeaderFormatter;
import com.foros.reporting.serializer.formatter.HintHeaderFormatter;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.PercentValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;
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
import com.foros.session.reporting.ReportHeaderFormatterRegistry;
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
@Stateless(name = "ProfilingReportService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class ProfilingReportService implements GenericReportService<ProfilingReportParameters, SimpleReportData> {

    private static final ValueFormatterRegistryImpl REGISTRY = ValueFormatterRegistries.registry()
            .column(ProfilingReportMeta.USERS_PROFILING_PC, new PercentValueFormatter(0))
            .column(ProfilingReportMeta.USERS_ADSERVING_PC, new PercentValueFormatter(0))
            .column(ProfilingReportMeta.PROFILING_REQ_PU, new NumberValueFormatter(0))
            .column(ProfilingReportMeta.ADSERVING_REQ_PU, new NumberValueFormatter(0))
            .column(ProfilingReportMeta.URL_HITS, new NumberValueFormatter(2))
            .column(ProfilingReportMeta.SEARCH_HITS, new NumberValueFormatter(2))
            .column(ProfilingReportMeta.KEYWORD_HITS, new NumberValueFormatter(2))
            .column(ProfilingReportMeta.CHANNELS_PER_PROFILE, new NumberValueFormatter(0));

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
    @Validate(validation = "Reporting.profilingReport", parameters = "#parameters")
    public void processHtml(ProfilingReportParameters parameters, SimpleReportData data) {
        processReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.profilingReport", parameters = "#parameters")
    public void processExcel(ProfilingReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.profilingReport", parameters = "#parameters")
    public void processCsv(ProfilingReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processReport(ProfilingReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer));
    }

    private class Report extends CommonAuditableReportSupport<ProfilingReportParameters> {
        public Report(ProfilingReportParameters parameters, AuditResultHandlerWrapper handler) {
            super(parameters, handler, false);
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

            handler.registry(REGISTRY, RowTypes.data());
            handler.registry(new ReportHeaderFormatterRegistry() {
                @Override
                public ValueFormatter<Column> get(Column column) {
                    if (ProfilingReportMeta.HINT_COLUMNS.contains(column)) {
                        return new HintHeaderFormatter(HeaderFormatter.INSTANCE);
                    }
                    return super.get(column);
                }
            }, RowTypes.header());

            metaData = ProfilingReportMeta.PROFILING_REPORT.resolve(parameters);

            if (currentUserService.isExternal()) {
                metaData = metaData.exclude(
                        ProfilingReportMeta.ADSERVING_REQ,
                        ProfilingReportMeta.ADSERVING_REQ_PU,
                        ProfilingReportMeta.CHANNELS_PER_PROFILE,
                        ProfilingReportMeta.EMPTY_PROFILES
                );
            }

            query = statsDb.queryFunction("report.profiling_targeting_overview")
                .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                .parameter("p_isp_id ", parameters.getAccountId(), Types.INTEGER);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.PROFILING;
        }
    }
}
