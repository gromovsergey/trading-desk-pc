package com.foros.session.reporting.conversionPixels;

import static com.foros.session.reporting.conversionPixels.ConversionPixelsMeta.CONVERSIONS;
import static com.foros.session.reporting.conversionPixels.ConversionPixelsMeta.REQUESTS;
import static com.foros.session.reporting.conversionPixels.ConversionPixelsMeta.REQUESTS_FROM_OPTED_IN;
import static com.foros.session.reporting.conversionPixels.ConversionPixelsMeta.REVENUE_CONVERSIONS;
import static com.foros.session.reporting.conversionPixels.ConversionPixelsMeta.REVENUE_TOTAL;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.HighlightValueFormatterWrapper;
import com.foros.reporting.serializer.formatter.HtmlCellAccessor;
import com.foros.reporting.serializer.formatter.NullValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;
import com.foros.reporting.serializer.formatter.registry.DefaultFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.tools.ResultHandlerWrapper;
import com.foros.reporting.tools.query.parameters.usertype.PostgreIntArrayUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.OutputType;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringEscapeUtils;

@LocalBean
@Stateless(name = "ConversionPixelsReportService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class ConversionPixelsReportService implements GenericReportService<ConversionPixelsReportParameters, SimpleReportData> {

    @EJB
    private ReportsService reportsService;

    @EJB
    private StatsDbQueryProvider statDb;

    @EJB
    private AdvertiserEntityRestrictions entityRestrictions;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    @Restrict(restriction = "Report.run", parameters = "'conversionPixels'")
    @Validate(validation = "Reporting.conversionPixels", parameters = "#parameters")
    public void processExcel(ConversionPixelsReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'conversionPixels'")
    @Validate(validation = "Reporting.conversionPixels", parameters = "#parameters")
    public void processCsv(ConversionPixelsReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'conversionPixels'")
    @Validate(validation = "Reporting.conversionPixels", parameters = "#parameters")
    public void processHtml(ConversionPixelsReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data));
    }

    private void run(ConversionPixelsReportParameters parameters, AuditResultHandlerWrapper handler) {
        reportsService.execute(new Report(parameters, handler));
    }

    private class Report extends CommonAuditableReportSupport<ConversionPixelsReportParameters> {

        public Report(ConversionPixelsReportParameters parameters, AuditResultHandlerWrapper handler) {
            super(parameters, handler, false);
        }

        @Override
        public void prepare() {
            final Account account = em.find(Account.class, parameters.getAccountId());

            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    String unitOfTime = parameters.isShowResultsByDay() ? StringUtil.getLocalizedString("report.output.field.date") :
                            StringUtil.getLocalizedString("report.output.field.summary");
                    builder.addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone())
                        .add("unitOfTime", unitOfTime, unitOfTime)
                        .addId("account", Account.class, parameters.getAccountId())
                        .addIds("advertisers", AdvertiserAccount.class, parameters.getConversionAdvertiserIds())
                        .addIds("conversions", Action.class, parameters.getConversionIds());
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            ValueFormatterRegistry registry = ValueFormatterRegistries.registry()
                    .column(ConversionPixelsMeta.CONVERSION_NAME, new EntityUrlValueFormatter(ConversionPixelsMeta.CONVERSION_ID, ConversionPixelsMeta.CONVERSION_URL_PATTERN) {
                        @Override
                        public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
                            if (entityRestrictions.canView()) {
                                // url
                                super.formatHtml(cellAccessor, value, context);
                            } else {
                                // simple text
                                String text = formatText(value, context);
                                cellAccessor.setHtml(StringEscapeUtils.escapeHtml(text));
                            }
                        }
                    })
                    .type(ColumnTypes.currency(), new CurrencyValueFormatter(account.getCurrency().getCurrencyCode()));

            handler.registry(registry, RowTypes.data());

            if (handler.getOutputType() == OutputType.HTML) {
                DbColumn firstColumn = parameters.isShowResultsByDay() ? ConversionPixelsMeta.DATE : ConversionPixelsMeta.CONVERSION_NAME;
                handler.registry(new TotalRegistry(firstColumn, registry), RowTypes.total());
            }

            String functionName;
            if (parameters.isShowResultsByDay()) {
                functionName = "report.conversion_pixels_report_by_date";
                metaData = ConversionPixelsMeta.META_BY_DATE.resolve(parameters);
            } else {
                functionName = "report.conversion_pixels_report";
                metaData = ConversionPixelsMeta.META.resolve(parameters);
            }

            query = statDb.queryFunction(functionName)
                .parameter("p_from_date", parameters.getDateRange().getBegin(), PostgreLocalDateUserType.INSTANCE)
                .parameter("p_to_date", parameters.getDateRange().getEnd(), PostgreLocalDateUserType.INSTANCE)
                .parameter("p_account_id", parameters.getAccountId(), Types.INTEGER)
                .parameter("p_advertiser_ids", parameters.getConversionAdvertiserIds(), PostgreIntArrayUserType.INSTANCE)
                .parameter("p_action_ids", parameters.getConversionIds(), PostgreIntArrayUserType.INSTANCE);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CONVERSION_PIXELS;
        }

        @Override
        protected ResultHandler wrap(ResultSerializer handler) {
            return this.handler.getOutputType() == OutputType.HTML ? new ConversionPixelsTotalHandlerWrapper(handler) : handler;
        }
    }

    private class ConversionPixelsTotalHandlerWrapper extends ResultHandlerWrapper {
        private TotalRow totalRow = new TotalRow();

        private ConversionPixelsTotalHandlerWrapper(ResultHandler resultHandler) {
            super(resultHandler);
        }

        @Override
        public void row(Row row) {
            super.row(row);
            totalRow.processRow(row);
        }

        @Override
        public void after() {
            if (totalRow.isFilled()) {
                super.row(totalRow);
            }
            super.after();
        }
    }

    private static class TotalRow implements Row {
        private AggregateFunction allRequests = ConversionPixelsMeta.REQUESTS.newAggregateFunction();
        private AggregateFunction optInRequests = ConversionPixelsMeta.REQUESTS_FROM_OPTED_IN.newAggregateFunction();
        private AggregateFunction conversions = ConversionPixelsMeta.CONVERSIONS.newAggregateFunction();
        private AggregateFunction allRevenue = ConversionPixelsMeta.REVENUE_TOTAL.newAggregateFunction();
        private AggregateFunction allRevenueConversions = ConversionPixelsMeta.REVENUE_CONVERSIONS.newAggregateFunction();

        private boolean filled;

        @Override
        public Object get(Column column) {
            if (column.equals(ConversionPixelsMeta.REQUESTS)) {
                return allRequests.getValue();
            } else if (column.equals(ConversionPixelsMeta.REQUESTS_FROM_OPTED_IN)) {
                return optInRequests.getValue();
            } else if (column.equals(ConversionPixelsMeta.CONVERSIONS)) {
                return conversions.getValue();
            } else if (column.equals(ConversionPixelsMeta.REVENUE_TOTAL)) {
                return allRevenue.getValue();
            } else if (column.equals(ConversionPixelsMeta.REVENUE_CONVERSIONS)) {
                return allRevenueConversions.getValue();
            }
            return null;
        }

        public void processRow(Row row) {
            allRequests.aggregate(row);
            optInRequests.aggregate(row);
            conversions.aggregate(row);
            allRevenue.aggregate(row);
            allRevenueConversions.aggregate(row);
            filled = true;
        }

        @Override
        public RowType getType() {
            return RowTypes.total();
        }

        public boolean isFilled() {
            return filled;
        }
    }

    private static class TotalRegistry implements ValueFormatterRegistry {
        private static final List<? extends Column> COLUMNS = Arrays.asList(REQUESTS, REQUESTS_FROM_OPTED_IN, CONVERSIONS, REVENUE_TOTAL, REVENUE_CONVERSIONS);
        private static final String CSS_CLASS = "total";
        private final Column first;
        private final ValueFormatterRegistry registry;

        private TotalRegistry(Column first, ValueFormatterRegistry registry) {
            this.first = first;
            this.registry = ValueFormatterRegistries.chain()
                    .registry(DefaultFormatterRegistry.DEFAULT_REGISTRY)
                    .registry(registry);
        }

        @Override
        public <T> ValueFormatter<T> get(Column column) {
            ValueFormatter result;
            if (column.equals(first)) {
                result = new HighlightValueFormatterWrapper(new ValueFormatterSupport<Object>() {
                    @Override
                    public String formatText(Object value, FormatterContext context) {
                        return StringUtil.getLocalizedString("report.total");
                    }
                }, CSS_CLASS + " totalText");
            } else if (COLUMNS.contains(column)) {
                result = new HighlightValueFormatterWrapper(registry.get(column), CSS_CLASS);
            } else {
                result = NullValueFormatter.INSTANCE;
            }
            return result;
        }
    }
}
