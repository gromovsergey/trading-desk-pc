package com.foros.session.reporting.campaignOverview;

import com.foros.model.account.Account;
import com.foros.model.account.IspAccount;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
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
@Stateless(name = "CampaignOverviewReportService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class CampaignOverviewReportService implements GenericReportService<CampaignOverviewReportParameters, SimpleReportData> {

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
    @Validate(validation = "Reporting.campaignOverviewReport", parameters = "#parameters")
    public void processHtml(CampaignOverviewReportParameters parameters, SimpleReportData data) {
        processReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.campaignOverviewReport", parameters = "#parameters")
    public void processExcel(CampaignOverviewReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.campaignOverviewReport", parameters = "#parameters")
    public void processCsv(CampaignOverviewReportParameters parameters, OutputStream stream) {
        processReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processReport(CampaignOverviewReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer));
    }

    private class Report extends CommonAuditableReportSupport<CampaignOverviewReportParameters> {
        public Report(CampaignOverviewReportParameters parameters, AuditResultHandlerWrapper handler) {
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

            IspAccount account = em.find(IspAccount.class, parameters.getAccountId());
            String countryCode = account.getCurrency().getCurrencyCode();
            ValueFormatterRegistry registry = ValueFormatterRegistries.registry()
                .type(ColumnTypes.currency(), new CurrencyValueFormatter(countryCode))
                .column(CampaignOverviewReportMeta.ECPC, new CurrencyValueFormatter(countryCode, 2))
                .column(CampaignOverviewReportMeta.ECPM, new CurrencyValueFormatter(countryCode, 2));
            handler.registry(registry, RowTypes.data());

            summaryMetaData = CampaignOverviewReportMeta.CAMPAIGN_OVERVIEW_REPORT_SUMMARY.resolve(parameters);
            summaryQuery = statsDb.queryFunction("report.campaign_overview_summary")
                .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                .parameter("p_isp_id ", parameters.getAccountId(), Types.INTEGER);

            prepareMetadata();

            query = statsDb.queryFunction("report.campaign_overview")
                .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                .parameter("p_isp_id ", parameters.getAccountId(), Types.INTEGER)
                .parameter("p_by_vertical", parameters.isSegmentByVertical())
                .parameter("p_by_product", parameters.isSegmentByProduct());
        }

        private void prepareMetadata() {
            metaData = CampaignOverviewReportMeta.CAMPAIGN_OVERVIEW_REPORT.resolve(parameters);
            if (!parameters.isSegmentByProduct()) {
                metaData = metaData.exclude(CampaignOverviewReportMeta.PRODUCT);
            }

            if (!parameters.isSegmentByVertical()) {
                metaData = metaData.exclude(CampaignOverviewReportMeta.VERTICAL);
            }
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CAMPAIGN_OVERVIEW;
        }
    }
}
