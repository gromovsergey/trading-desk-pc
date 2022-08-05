package com.foros.session.reporting.isp;

import com.foros.model.account.Account;
import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.tools.query.Query;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportsService;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless(name = "ISPReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class ISPReportService implements GenericReportService<ISPReportParameters, SimpleReportData> {

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    private ReportsService reportsService;

    @EJB
    protected CurrentUserService currentUserService;

    @EJB
    private StatsDbQueryProvider statsDb;

    @Restrict(restriction = "Report.run", parameters = "'ISP'")
    @Validate(validation = "Reporting.ISP", parameters = "#parameters")
    public void processHtml(ISPReportParameters parameters, SimpleReportData data) {
        processISPReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Restrict(restriction = "Report.run", parameters = "'ISP'")
    @Validate(validation = "Reporting.ISP", parameters = "#parameters")
    public void processExcel(ISPReportParameters parameters, OutputStream stream) {
        processISPReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Restrict(restriction = "Report.run", parameters = "'ISP'")
    @Validate(validation = "Reporting.ISP", parameters = "#parameters")
    public void processCsv(ISPReportParameters parameters, OutputStream stream) {
        processISPReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processISPReport(ISPReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer));
    }

    private class Report extends CommonAuditableReportSupport<ISPReportParameters> {

        public Report(ISPReportParameters parameters, AuditResultHandlerWrapper handler) {
            super(parameters, handler, true);
        }

        @Override
        public void prepare() {
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    Account account = em.find(Account.class, parameters.getAccountId());
                    builder.addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone())
                            .addId("account", Account.class, parameters.getAccountId())
                            .addId("colocation", Colocation.class, parameters.getColocationId());
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            IspAccount account = em.find(IspAccount.class, parameters.getAccountId());
            String countryCode = account.getCurrency().getCurrencyCode();
            CurrencyValueFormatter arpuValueFormatter = new CurrencyValueFormatter(countryCode, 4);
            ValueFormatterRegistry registry = ValueFormatterRegistries.registry()
                .type(ColumnTypes.currency(), new CurrencyValueFormatter(countryCode))
                .column(ISPReportMeta.ARPU_DAILY, arpuValueFormatter)
                .column(ISPReportMeta.ARPU_RANGE, arpuValueFormatter);
            handler.registry(registry, RowTypes.data());

            String functionName;
            if (parameters.getReportType().equals(ISPReportType.BY_DATE)) {
                metaData = ISPReportMeta.BY_DATE.resolve(parameters);
                functionName = "report.isp_report_by_date";
            } else {
                DateRange dr = parameters.getDateRange();
                if (dr.getEnd().minusDays(ISPReportMeta.MIN_DATE_RANGE_FOR_TOTALS).compareTo(dr.getBegin()) < 0) {
                    metaData = ISPReportMeta.BY_COLOCATION.resolve(parameters);
                } else {
                    metaData = ISPReportMeta.BY_COLOCATION.resolve(parameters).exclude(ISPReportMeta.EXCLUDABLE_BY_MIN_DATE_RANGE);
                }
                functionName = "report.isp_report_by_colo";
            }

            if (currentUserService.isExternal()) {
                metaData = metaData.exclude(ISPReportMeta.EXCLUDABLE_FOR_EXTERNAL);
            }
            summaryMetaData = metaData.metricsOnly().exclude(ISPReportMeta.EXCLUDABLE_FROM_SUMMARY);
            query = createQuery(functionName);
            summaryQuery = createQuery(functionName+"_summary");
        }

        private Query createQuery(String functionName) {
            return statsDb.queryFunction(functionName)
                    .parameter("p_from_date", parameters.getDateRange().getBegin(), PostgreLocalDateUserType.INSTANCE)
                    .parameter("p_to_date", parameters.getDateRange().getEnd(), PostgreLocalDateUserType.INSTANCE)
                    .parameter("p_account_id", parameters.getAccountId())
                    .parameter("p_colo_id", parameters.getColocationId());
        }

        @Override
        public ReportType getReportType() {
            return ReportType.ISP;
        }
    }
}
