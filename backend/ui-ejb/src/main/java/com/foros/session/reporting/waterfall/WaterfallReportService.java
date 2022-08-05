package com.foros.session.reporting.waterfall;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.HrefFormatterWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.LocalDateValueFormatter;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.PercentValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
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

import java.io.OutputStream;
import java.sql.Types;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

@LocalBean
@Stateless(name = "WaterfallReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class WaterfallReportService implements GenericReportService<WaterfallReportParameters, SimpleReportData> {

    @EJB
    private ReportsService reportsService;

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private CurrentUserService currentUserService;

    private static final ValueFormatterRegistry REGISTRY = ValueFormatterRegistries.registry()
            .column(WaterfallMeta.DATE, new LocalDateValueFormatter())
            .column(WaterfallMeta.RELEVANT_REQUESTS, new NumberValueFormatter())
            .column(WaterfallMeta.OPPORTUNITIES_TO_SERVE, new NumberValueFormatter())
            .column(WaterfallMeta.IMPRESSIONS, new NumberValueFormatter())
            .column(WaterfallMeta.AUCTIONS_LOST, new NumberValueFormatter())
            .column(WaterfallMeta.SELECTION_FAILURES, new NumberValueFormatter())
            .column(WaterfallMeta.PUBLISHER_EXCLUSIONS, new PercentValueFormatter(0))
            .column(WaterfallMeta.SITE_TARGETING, new PercentValueFormatter(0))
            .column(WaterfallMeta.WALLED_GARDEN, new PercentValueFormatter(0))
            .column(WaterfallMeta.PUBLISHER_FC, new PercentValueFormatter(0))
            .column(WaterfallMeta.ADVERTISER_FC, new PercentValueFormatter(0))
            .column(WaterfallMeta.TIME_OF_DAY, new PercentValueFormatter(0));

    @Override
    @Restrict(restriction = "Report.Waterfall.run", parameters = "find('CampaignCreativeGroup', #parameters.ccgId)")
    public void processExcel(WaterfallReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os));
    }

    @Override
    @Restrict(restriction = "Report.Waterfall.run", parameters = "find('CampaignCreativeGroup', #parameters.ccgId)")
    public void processCsv(WaterfallReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os));
    }

    @Override
    @Restrict(restriction = "Report.Waterfall.run", parameters = "find('CampaignCreativeGroup', #parameters.ccgId)")
    public void processHtml(WaterfallReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data));
    }

    private void run(WaterfallReportParameters parameters, AuditResultHandlerWrapper handler) {
        reportsService.execute(new Report(parameters, handler, false));
    }

    private class Report extends CommonAuditableReportSupport<WaterfallReportParameters> {
        public Report(WaterfallReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {

            ValueFormatterRegistryImpl dynamicRegistry = ValueFormatterRegistries.registry()
                    .column(WaterfallMeta.SELECTION_FAILURES, new HrefFormatterWrapper(new NumberValueFormatter()) {
                        @Override
                        protected String getHref(String text, FormatterContext context) {
                            LocalDate date = (LocalDate) context.getRow().get(WaterfallMeta.DATE);
                            String pattern = DateTimeFormat.patternForStyle("S-", context.getLocale());
                            long ccgId = parameters.getCcgId();
                            return "../selectionFailures/run.action?ccgId=" + ccgId + "&date=" + date.toString(pattern);
                        }

                        @Override
                        protected String getStyle() {
                            return "number";
                        }
                    });
            handler
                    .registry(REGISTRY, RowTypes.data())
                    .registry(dynamicRegistry, RowTypes.data());

            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    builder.addId("creativeGroup", CampaignCreativeGroup.class, parameters.getCcgId());
                }
            };

            query = statsDb.queryFunction("report.ccg_waterfall")
                    .parameter("p_ccg_id", parameters.getCcgId(), Types.BIGINT);

            metaData = WaterfallMeta.META.resolve(parameters);

            handler.preparedParameters(preparedParameterBuilderFactory);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.WATERFALL;
        }
    }
}
