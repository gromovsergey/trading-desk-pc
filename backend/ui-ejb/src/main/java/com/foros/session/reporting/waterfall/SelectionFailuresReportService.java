package com.foros.session.reporting.waterfall;

import com.foros.model.Timezone;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.HrefFormatterWrapper;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.HeaderWithHintFomratter;
import com.foros.reporting.serializer.formatter.HighlightValueFormatterWrapper;
import com.foros.reporting.serializer.formatter.HtmlCellAccessor;
import com.foros.reporting.serializer.formatter.NumberWithPercentValueFormatter;
import com.foros.reporting.serializer.formatter.PercentColumnSuppliedValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.ResultHandlerWrapper;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.OutputType;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.validation.ValidationInterceptor;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless(name = "SelectionFailuresReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class SelectionFailuresReportService implements GenericReportService<SelectionFailuresReportParameters, SimpleReportData> {

    @EJB
    private ReportsService reportsService;

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private CurrentUserService currentUserService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    private static final ValueFormatterRegistry REGISTRY = ValueFormatterRegistries.registry()
            .column(SelectionFailuresMeta.PUBLISHER_EXCLUSIONS, new MaskValueFormatter())
            .column(SelectionFailuresMeta.SITE_TARGETING, new MaskValueFormatter())
            .column(SelectionFailuresMeta.WALLED_GARDEN, new MaskValueFormatter())
            .column(SelectionFailuresMeta.PUBLISHER_FC, new MaskValueFormatter())
            .column(SelectionFailuresMeta.ADVERTISER_FC, new MaskValueFormatter())
            .column(SelectionFailuresMeta.TIME_OF_DAY, new MaskValueFormatter())
            .column(SelectionFailuresMeta.SELECTION_FAILURES, new PercentColumnSuppliedValueFormatter(SelectionFailuresMeta.SELECTION_FAILURES_PC));

    private static final ValueFormatterRegistry WITH_TIP_REGISTRY = ValueFormatterRegistries.registry().column(SelectionFailuresMeta.SELECTION_FAILURES_DAILY, new HeaderWithHintFomratter("selectionFailuresDaily.tip"));

    @Restrict(restriction = "Report.Waterfall.run")
    public void processExcel(SelectionFailuresReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os));
    }

    @Restrict(restriction = "Report.Waterfall.run")
    public void processCsv(SelectionFailuresReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os));
    }

    @Restrict(restriction = "Report.Waterfall.run")
    public void processHtml(SelectionFailuresReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data));
    }

    private void run(SelectionFailuresReportParameters parameters, AuditResultHandlerWrapper handler) {
        reportsService.execute(new Report(parameters, handler, false));
    }

    private class Report extends CommonAuditableReportSupport<SelectionFailuresReportParameters> {
        public Report(SelectionFailuresReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            ValueFormatterRegistryImpl dynamicRegistry = ValueFormatterRegistries.registry()
                    .column(SelectionFailuresMeta.SELECTION_FAILURES_DAILY, new HrefFormatterWrapper(new PercentColumnSuppliedValueFormatter(SelectionFailuresMeta.SELECTION_FAILURES_DAILY_PC)) {
                        @Override
                        protected String getHref(String text, FormatterContext context) {
                            int mask = (Integer) context.getRow().get(SelectionFailuresMeta.MASK);
                            long ccgId = parameters.getCcgId();
                            StringBuilder sb = new StringBuilder()
                                    .append("../selectionFailuresTrend/run.action?ccgId=")
                                    .append(ccgId)
                                    .append("&mask=")
                                    .append(mask);
                            return sb.toString();
                        }

                        @Override
                        protected String getStyle() {
                            return "number";
                        }
                    });
            handler
                    .registry(REGISTRY, RowTypes.data())
                    .registry(dynamicRegistry, RowTypes.data())
                    .registry(WITH_TIP_REGISTRY, RowTypes.header());

            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    Long ccgId = parameters.getCcgId();
                    builder.addId("creativeGroup", CampaignCreativeGroup.class, ccgId);
                    Timezone timezone = em.find(CampaignCreativeGroup.class, ccgId).getAccount().getTimezone();
                    builder.addDate(parameters.getDate(), timezone.toTimeZone());
                }
            };

            query = statsDb.queryFunction("report.ccg_selection_failures")
                    .parameter("p_ccg_id", parameters.getCcgId(), Types.BIGINT)
                    .parameter("p_date", parameters.getDate(), Types.DATE);

            metaData = SelectionFailuresMeta.META.resolve(parameters);

            handler.preparedParameters(preparedParameterBuilderFactory);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.SELECTION_FAILURES;
        }

        @Override
        protected ResultHandler wrap(ResultSerializer handler) {
            ResultHandler wrapper = handler;
            if (getOutputType() == OutputType.HTML) {
                handler.registry(new TotalRegistry(), RowTypes.total());
                wrapper = new TotalHandlerWrapper(handler);
            }
            return wrapper;
        }
    }

    private static class TotalHandlerWrapper extends ResultHandlerWrapper {

        private TotalRow totalRow = new TotalRow();

        private boolean cycled;

        private TotalHandlerWrapper(ResultHandler resultHandler) {
            super(resultHandler);
        }

        @Override
        public void row(Row row) {
            super.row(row);
            cycled = true;
            totalRow.processRow(row);
        }

        @Override
        public void after() {
            if (cycled) {
                super.row(totalRow);
            }
            super.after();
        }
    }

    private static class TotalRow implements Row {

        private Map<Column, AggregateFunction> sums = new IdentityHashMap<>(8);

        TotalRow() {
            map(SelectionFailuresMeta.PUBLISHER_EXCLUSIONS);
            map(SelectionFailuresMeta.SITE_TARGETING);
            map(SelectionFailuresMeta.WALLED_GARDEN);
            map(SelectionFailuresMeta.PUBLISHER_FC);
            map(SelectionFailuresMeta.ADVERTISER_FC);
            map(SelectionFailuresMeta.TIME_OF_DAY);
            map(SelectionFailuresMeta.SELECTION_FAILURES);
            map(SelectionFailuresMeta.SELECTION_FAILURES_DAILY);
        }

        private AggregateFunction map(DbColumn column) {
            AggregateFunction function = column.newAggregateFunction();
            sums.put(column, function);
            return function;
        }

        @Override
        public Object get(Column column) {
            return sums.get(column).getValue();
        }

        void processRow(Row row) {
            for (AggregateFunction function : sums.values()) {
                function.aggregate(row);
            }
        }

        @Override
        public RowType getType() {
            return RowTypes.total();
        }
    }

    private static class TotalRegistry implements ValueFormatterRegistry {

        @Override
        public <T> ValueFormatter<T> get(Column column) {
            ValueFormatter result;
            if (column == SelectionFailuresMeta.PUBLISHER_EXCLUSIONS ||
                    column == SelectionFailuresMeta.SITE_TARGETING ||
                    column == SelectionFailuresMeta.WALLED_GARDEN ||
                    column == SelectionFailuresMeta.PUBLISHER_FC ||
                    column == SelectionFailuresMeta.ADVERTISER_FC ||
                    column == SelectionFailuresMeta.TIME_OF_DAY) {
                result = new HighlightValueFormatterWrapper(new NumberWithPercentValueFormatter() {
                    @Override
                    protected BigDecimal getPercent(FormatterContext context, BigDecimal value) {
                        BigDecimal total = (BigDecimal) context.getRow().get(SelectionFailuresMeta.SELECTION_FAILURES);
                        return value.divide(total, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    }
                }, "total");
            } else if (column == SelectionFailuresMeta.SELECTION_FAILURES || column == SelectionFailuresMeta.SELECTION_FAILURES_DAILY) {
                result = new HighlightValueFormatterWrapper(new NumberWithPercentValueFormatter() {
                    @Override
                    protected BigDecimal getPercent(FormatterContext context, BigDecimal value) {
                        return BigDecimal.valueOf(100);
                    }
                }, "total");
            } else {
                result = new HighlightValueFormatterWrapper(REGISTRY.get(column), "total");
            }
            return result;
        }
    }

    private static class MaskValueFormatter extends ValueFormatterSupport<BigDecimal> {

        @Override
        public String formatText(BigDecimal value, FormatterContext context) {
            return value.compareTo(BigDecimal.ZERO) != 0 ? "x" : "";
        }

        @Override
        public void formatHtml(HtmlCellAccessor cellAccessor, BigDecimal value, FormatterContext context) {
            cellAccessor.addStyle("radio");
            super.formatHtml(cellAccessor, value, context);
        }
    }
}
