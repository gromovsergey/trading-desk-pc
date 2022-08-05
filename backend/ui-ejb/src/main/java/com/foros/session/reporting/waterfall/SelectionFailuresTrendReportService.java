package com.foros.session.reporting.waterfall;

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
import com.foros.reporting.serializer.formatter.HighlightValueFormatterWrapper;
import com.foros.reporting.serializer.formatter.LocalDateValueFormatter;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
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
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

@LocalBean
@Stateless(name = "SelectionFailuresTrendReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class SelectionFailuresTrendReportService implements GenericReportService<SelectionFailuresTrendReportParameters, SimpleReportData> {

    @EJB
    private ReportsService reportsService;

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private CurrentUserService currentUserService;

    private static final ValueFormatterRegistry REGISTRY = ValueFormatterRegistries.registry()
            .column(SelectionFailuresTrendMeta.DATE, new LocalDateValueFormatter())
            .column(SelectionFailuresTrendMeta.COMBINATION, new PercentColumnSuppliedValueFormatter(SelectionFailuresTrendMeta.COMBINATION_PC));

    @Restrict(restriction = "Report.Waterfall.run")
    public void processExcel(SelectionFailuresTrendReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os));
    }

    @Restrict(restriction = "Report.Waterfall.run")
    public void processCsv(SelectionFailuresTrendReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os));
    }

    @Restrict(restriction = "Report.Waterfall.run")
    public void processHtml(SelectionFailuresTrendReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data));
    }

    private void run(SelectionFailuresTrendReportParameters parameters, AuditResultHandlerWrapper handler) {
        reportsService.execute(new Report(parameters, handler, false));
    }

    private class Report extends CommonAuditableReportSupport<SelectionFailuresTrendReportParameters> {
        public Report(SelectionFailuresTrendReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            ValueFormatterRegistryImpl dynamicRegistry = ValueFormatterRegistries.registry()
                    .column(SelectionFailuresTrendMeta.TOTAL_BY_DATE, new HrefFormatterWrapper(new NumberValueFormatter()) {
                        @Override
                        protected String getHref(String text, FormatterContext context) {
                            LocalDate date = (LocalDate) context.getRow().get(SelectionFailuresTrendMeta.DATE);
                            String pattern = DateTimeFormat.patternForStyle("S-", context.getLocale());
                            long ccgId = parameters.getCcgId();
                            StringBuilder sb = new StringBuilder()
                                    .append("../selectionFailures/run.action?ccgId=")
                                    .append(ccgId)
                                    .append("&date=")
                                    .append(date.toString(pattern));
                            return sb.toString();
                        }

                        @Override
                        protected String getStyle() {
                            return "number";
                        }
                    });
            handler.registry(REGISTRY, RowTypes.data()).registry(dynamicRegistry, RowTypes.data());
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    builder.addId("creativeGroup", CampaignCreativeGroup.class, parameters.getCcgId());
                    builder.add("selectionFailureCombination", getCombinationText(parameters.getMask()));
                }

            };

            query = statsDb.queryFunction("report.ccg_selection_failures_trend")
                    .parameter("p_ccg_id", parameters.getCcgId(), Types.BIGINT)
                    .parameter("p_mask", parameters.getMask(), Types.INTEGER);

            metaData = SelectionFailuresTrendMeta.META.resolve(parameters);

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

    private void appendToCombinationText(StringBuilder sb, DbColumn column, int mask) {
        int number = Integer.valueOf(column.getResultSetName().replaceAll("failures_", ""));
        if ((mask & (1 << number)) > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(StringUtil.getLocalizedString(column.getNameKey()));
        }
    }

    private String getCombinationText(int mask) {
        StringBuilder sb = new StringBuilder();
        appendToCombinationText(sb, SelectionFailuresMeta.PUBLISHER_EXCLUSIONS, mask);
        appendToCombinationText(sb, SelectionFailuresMeta.SITE_TARGETING, mask);
        appendToCombinationText(sb, SelectionFailuresMeta.WALLED_GARDEN, mask);
        appendToCombinationText(sb, SelectionFailuresMeta.PUBLISHER_FC, mask);
        appendToCombinationText(sb, SelectionFailuresMeta.ADVERTISER_FC, mask);
        appendToCombinationText(sb, SelectionFailuresMeta.TIME_OF_DAY, mask);
        return sb.toString();
    }

    private static class TotalRow implements Row {

        private AggregateFunction combinationSum = SelectionFailuresTrendMeta.COMBINATION.newAggregateFunction();
        private AggregateFunction dateTotal = SelectionFailuresTrendMeta.TOTAL_BY_DATE.newAggregateFunction();

        @Override
        public Object get(Column column) {
            if (column == SelectionFailuresTrendMeta.COMBINATION) {
                return combinationSum.getValue();
            } else if (column == SelectionFailuresTrendMeta.TOTAL_BY_DATE) {
                return dateTotal.getValue();
            } else {
                return null;
            }
        }

        void processRow(Row row) {
            combinationSum.aggregate(row);
            dateTotal.aggregate(row);
        }

        @Override
        public RowType getType() {
            return RowTypes.total();
        }
    }

    private static class TotalHandlerWrapper extends ResultHandlerWrapper {

        private TotalRow totalRow = new TotalRow();

        private boolean cycled;

        TotalHandlerWrapper(ResultHandler resultHandler) {
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

    private static class TotalRegistry implements ValueFormatterRegistry {

        @Override
        public <T> ValueFormatter<T> get(Column column) {
            ValueFormatter result = null;
            if (column == SelectionFailuresTrendMeta.DATE) {
                result = new HighlightValueFormatterWrapper(new ValueFormatterSupport<Object>() {
                    @Override
                    public String formatText(Object value, FormatterContext context) {
                        return StringUtil.getLocalizedString("report.total");
                    }
                }, "total totalText");
            } else if (column == SelectionFailuresTrendMeta.COMBINATION) {
                result = new HighlightValueFormatterWrapper(new NumberWithPercentValueFormatter() {
                    @Override
                    protected BigDecimal getPercent(FormatterContext context, BigDecimal value) {
                        BigDecimal totalFailures = (BigDecimal) context.getRow().get(SelectionFailuresTrendMeta.TOTAL_BY_DATE);
                        return value.divide(totalFailures, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    }
                }, "total");
            } else if (column == SelectionFailuresTrendMeta.TOTAL_BY_DATE) {
                result = new HighlightValueFormatterWrapper(new NumberValueFormatter(), "total");
            }
            return result;
        }
    }
}
