package com.foros.session.reporting.pubAdvertising;

import com.foros.model.account.Account;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.DefaultValueFormatter;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.LocalizableValueFormatter;
import com.foros.reporting.serializer.formatter.StatusableEntityUrlValueFormatter;
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
@Stateless(name = "PubAdvertisingReportService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class PubAdvertisingReportService implements GenericReportService<PubAdvertisingReportParameters, SimpleReportData> {

    private static final String DIRECT_ADVERTISER_KEY = "reporting.directAdvertiser";
    private static final String SITE_URL_PATTERN_INTERNAL = "../../../site/view.action?id=%d";
    private static final String SITE_URL_PATTERN_EXTERNAL = "../../site/view.action?id=%d";
    private static final String ADVERTISER_URL_PATTERN = "../../../account/view.action?id=%d";

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    protected StatsDbQueryProvider statsDb;

    @EJB
    private ReportsService reportsService;

    @EJB
    protected CurrentUserService currentUserService;

    @Override
    @Restrict(restriction = "Report.PubAdvertisingReport.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.pubAdvertisingReport", parameters = "#parameters")
    public void processHtml(PubAdvertisingReportParameters parameters, SimpleReportData data) {
        processReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.PubAdvertisingReport.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.pubAdvertisingReport", parameters = "#parameters")
    public void processExcel(PubAdvertisingReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.PubAdvertisingReport.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.pubAdvertisingReport", parameters = "#parameters")
    public void processCsv(PubAdvertisingReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processReport(PubAdvertisingReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer));
    }

    private ValueFormatterRegistryImpl getRegistry(boolean isExternal, String countryCode) {
        ValueFormatterRegistryImpl result = ValueFormatterRegistries.registry()
            .column(PubAdvertisingReportMeta.AGENCY,
                    new LocalizableValueFormatter(DIRECT_ADVERTISER_KEY, new DefaultValueFormatter()))
            .type(ColumnTypes.currency(), new CurrencyValueFormatter(countryCode));

        if (isExternal) {
            result
                .column(PubAdvertisingReportMeta.SITE_NAME,
                    StatusableEntityUrlValueFormatter.html(PubAdvertisingReportMeta.SITE_ID, SITE_URL_PATTERN_EXTERNAL, PubAdvertisingReportMeta.SITE_STATUS));
        } else {
            result
                .column(PubAdvertisingReportMeta.SITE_NAME,
                    EntityUrlValueFormatter.html(PubAdvertisingReportMeta.SITE_ID, SITE_URL_PATTERN_INTERNAL))
                .column(PubAdvertisingReportMeta.ADVERTISER_NAME,
                    EntityUrlValueFormatter.html(PubAdvertisingReportMeta.ADVERTISER_ID, ADVERTISER_URL_PATTERN));
        }

        return result;
    }

    private class Report extends CommonAuditableReportSupport<PubAdvertisingReportParameters> {
        public Report(PubAdvertisingReportParameters parameters, AuditResultHandlerWrapper handler) {
            super(parameters, handler, true);
        }

        @Override
        public void prepare() {
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    Account account = em.find(Account.class, parameters.getAccountId());
                    builder.addDateRange(parameters.getDateRange(), account.getCountry().getTimezone().toTimeZone());
                    builder.addId("account", Account.class, parameters.getAccountId());
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            handler.registry(getRegistry(currentUserService.isExternal(), parameters.getCountryCode()), RowTypes.data());

            summaryMetaData = PubAdvertisingReportMeta.PUB_ADVERTISING_REPORT_SUMMARY.resolve(parameters);
            metaData = PubAdvertisingReportMeta.PUB_ADVERTISING_REPORT.resolve(parameters);
            if (!currentUserService.isInternal()) {
                summaryMetaData = summaryMetaData.exclude(PubAdvertisingReportMeta.INTERNAL_ONLY);
                metaData = metaData.exclude(PubAdvertisingReportMeta.INTERNAL_ONLY);
            }

            summaryQuery = statsDb.queryFunction("report.publisher_advertisers_summary")
                    .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                    .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                    .parameter("p_publisher_id ", parameters.getAccountId(), Types.INTEGER)
                    .parameter("p_user_id", currentUserService.isSiteLevelRestricted() ? currentUserService.getUserId() : null, Types.INTEGER);

            query = statsDb.queryFunction("report.publisher_advertisers")
                    .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                    .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                    .parameter("p_publisher_id ", parameters.getAccountId(), Types.INTEGER)
                    .parameter("p_user_id", currentUserService.isSiteLevelRestricted() ? currentUserService.getUserId() : null, Types.INTEGER);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.PUB_ADVERTISING;
        }
    }
}
