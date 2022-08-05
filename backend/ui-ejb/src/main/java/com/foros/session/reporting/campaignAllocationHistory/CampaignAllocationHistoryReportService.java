package com.foros.session.reporting.campaignAllocationHistory;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;

import com.foros.model.campaign.Campaign;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.HtmlCellAccessor;
import com.foros.reporting.serializer.formatter.LocalDateTimeValueFormatter;
import com.foros.reporting.serializer.formatter.NAValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.campaign.CampaignService;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;

import java.io.OutputStream;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.type.IntegerType;

@LocalBean
@Stateless(name = "CampaignAllocationHistoryReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class CampaignAllocationHistoryReportService implements GenericReportService<Long, SimpleReportData> {

    public static final DbColumn START_DATE = buildColumn("startDate", "start_date", ColumnTypes.dateTime());
    public static final DbColumn END_DATE = buildColumn("endDate", "end_date", ColumnTypes.dateTime());
    public static final DbColumn TYPE = buildColumn("allocation.type", "type", ColumnTypes.string());
    public static final DbColumn ALLOCATION_ID = buildColumn("allocation.id", "allocation_id", ColumnTypes.id());
    public static final DbColumn ALLOCATION_NUMBER = buildColumn("allocation.number", "allocation_number", ColumnTypes.string(), ALLOCATION_ID);
    public static final DbColumn UTILISED_AMOUNT = buildColumn("utilizedAmount", "utilized_amount", ColumnTypes.currency());

    public static final ResolvableMetaData<DbColumn> META_DATA = metaData("channelSitesReport")
            .outputColumns(START_DATE, END_DATE, TYPE, ALLOCATION_NUMBER)
            .metricsColumns(UTILISED_AMOUNT)
            .build();

    private static final String IO_URL_PATTERN = "../../insertionOrder/view.action?id=%d&campaignId=";

    @EJB
    private ReportsService reportsService;

    @EJB
    private CampaignService campaignService;

    @EJB
    private StatsDbQueryProvider statDb;

    @Override
    @Restrict(restriction = "CampaignAllocation.view", parameters = "#campaignId")
    public void processHtml(Long campaignId, SimpleReportData data) {
        process(campaignId, reportsService.createHtmlSerializer(data), true);
    }

    @Override
    @Restrict(restriction = "CampaignAllocation.view", parameters = "#campaignId")
    public void processExcel(Long campaignId, OutputStream stream) {
        Campaign campaign = campaignService.find(campaignId);
        String title = StringUtil.getLocalizedString("reports.campaignAllocationHistory") + ":" + campaign.getName();
        process(campaignId, reportsService.createExcelSerializer(stream, title), true);
    }

    @Override
    @Restrict(restriction = "CampaignAllocation.view", parameters = "#campaignId")
    public void processCsv(Long campaignId, OutputStream stream) {
        process(campaignId, reportsService.createCsvSerializer(stream), false);
    }

    private void process(Long campaignId, AuditResultHandlerWrapper serializer, boolean summary) {
        reportsService.execute(new Report(campaignId, serializer, summary));
    }

    private class Report extends CommonAuditableReportSupport<Long> {

        private Campaign campaign;

        public Report(Long campaignId, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(campaignId, handler, executeSummary);
        }

        @Override
        public void prepare() {
            campaign = campaignService.find(parameters);

            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                }
            };

            handler.registry(buildRegistry(), RowTypes.data());

            handler.preparedParameters(preparedParameterBuilderFactory);

            metaData = META_DATA.resolve(parameters);
            summaryMetaData = metaData.metricsOnly();

            query = statDb.queryFunction("entityqueries.campaign_allocation_history")
                .parameter("campaignId", campaign.getId().intValue(), IntegerType.INSTANCE);

            summaryQuery = statDb.queryFunction("entityqueries.campaign_allocation_history_summary")
                .parameter("campaignId", campaign.getId().intValue(), IntegerType.INSTANCE);

        }



        private ValueFormatterRegistry buildRegistry() {
            final String ongoingStr = StringUtil.getLocalizedString("report.campaignAssociation.history.ongoing");
            final String ioStr = StringUtil.getLocalizedString("report.campaignAssociation.history.type.io");
            final String ccStr = StringUtil.getLocalizedString("campaignAllocation.status.campaignCredit");
            final String ioNumberStr = StringUtil.getLocalizedString("opportunity.ioNumber") + " ";

            return ValueFormatterRegistries.registry()
                .column(ALLOCATION_NUMBER, new EntityUrlValueFormatter(ALLOCATION_ID, IO_URL_PATTERN + campaign.getId()) {
                    @Override
                    public String formatText(String value, FormatterContext context) {
                        if (context.getRow().get(TYPE).equals("IO")) {
                            return ioNumberStr + super.formatText(value, context);
                        }
                        return StringUtil.getLocalizedString("CampaignCredit.id", super.formatText(value, context));
                    }

                    @Override
                    public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
                        if (context.getRow().get(TYPE).equals("IO")) {
                            // url
                            super.formatHtml(cellAccessor, value, context);
                        } else {
                            // simple text
                            String text = formatText(value, context);
                            cellAccessor.setHtml(StringEscapeUtils.escapeHtml(text));
                        }
                    }
                })
                .column(END_DATE, new NAValueFormatter(new LocalDateTimeValueFormatter()) {
                    @Override
                    protected String getNAValue(Locale locale) {
                        return ongoingStr;
                    }

                    @Override
                    protected boolean isNotAvailable(Object value, FormatterContext context) {
                        return value == null && context.getRow().get(TYPE).equals("IO");
                    }
                })
                .column(TYPE, new ValueFormatterSupport<String>() {
                    @Override
                    public String formatText(String value, FormatterContext context) {
                        if ("IO".equals(value)) {
                            return ioStr;
                        }
                        if ("CC".equals(value)) {
                            return ccStr;
                        }
                        return value;
                    }
                })
                .column(UTILISED_AMOUNT, new CurrencyValueFormatter(campaign.getAccount().getCurrency().getCurrencyCode()));
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CAMPAIGN_ALLOCATION_HISTORY;
        }
    }
}
