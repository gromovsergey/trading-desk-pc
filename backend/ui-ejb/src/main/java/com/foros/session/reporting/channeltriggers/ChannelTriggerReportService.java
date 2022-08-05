package com.foros.session.reporting.channeltriggers;

import com.foros.cache.generic.interceptor.CacheInterceptor;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnOrder;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.HtmlCell;
import com.foros.reporting.serializer.formatter.EllipsisedValueFormatter;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.UrlValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.Query;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLongArrayUserType;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.ImpalaDbQueryProvider;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.AuditableReportSupport;
import com.foros.session.reporting.PreparedParameter;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.session.reporting.parameters.ColumnOrderTO;
import com.foros.session.reporting.parameters.Order;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.hibernate.usertype.UserType;
import org.apache.commons.lang.StringUtils;

@LocalBean
@Stateless(name = "ChannelTriggerReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, CacheInterceptor.class})
public class ChannelTriggerReportService {

    private static final UserType CHANNEL_IDS_USER_TYPE = new PostgreLongArrayUserType();

    @EJB
    private ReportsService reportsService;

    @EJB
    private ImpalaDbQueryProvider impalaDb;

    @EJB
    private ConfigService configService;

    @Restrict(restriction = "Report.ChannelTriggers.run", parameters = "#parameters")
    @Validate(validation = "Reporting.channelTriggers", parameters = "#parameters")
    public void processHtml(ChannelTriggersReportParameters parameters, String urlPattern, ChannelTriggerReportData data) {
        EllipsisedValueFormatter ellipsisedValueFormatter = new EllipsisedValueFormatter(80, 60, 15, 5);
        ValueFormatterRegistryImpl registry = ValueFormatterRegistries.registry()
                .column(ChannelTriggersMeta.CHANNEL, EntityUrlValueFormatter.html(ChannelTriggersMeta.CHANNEL_ID, urlPattern))
                .column(ChannelTriggersMeta.KEYWORD, ellipsisedValueFormatter)
                .column(ChannelTriggersMeta.URL, new UrlTriggerFormatter(ellipsisedValueFormatter));

        ChannelTriggerResultHolder holder = new ChannelTriggerResultHolder(
                configService.get(ConfigParameters.HTML_REPORT_MAX_ROWS),
                null,
                CurrentUserSettingsHolder.getLocale(), data);

        AuditResultHandlerWrapper serializer = reportsService.createHtmlSerializer(holder);
        serializer.registry(registry, RowTypes.data());
        processChannelTriggersReport(parameters, serializer);
    }

    @Restrict(restriction = "Report.ChannelTriggers.run", parameters = "#parameters")
    @Validate(validation = "Reporting.channelTriggers", parameters = "#parameters")
    public void processExcel(ChannelTriggersReportParameters parameters, OutputStream stream) {
        processChannelTriggersReport(parameters, reportsService.createExcelSerializer(stream, StringUtil.getLocalizedString("reports.channelTriggersReport")));
    }

    public List<PreparedParameter> prepareParameters(ChannelTriggersReportParameters parameters) {
        return parametersFactory(parameters)
                .builder()
                .parameters();
    }

    private void processChannelTriggersReport(final ChannelTriggersReportParameters parameters, AuditResultHandlerWrapper handler) {
        reportsService.execute(new Report(parameters, handler));
    }

    private PreparedParameterBuilder.Factory parametersFactory(final ChannelTriggersReportParameters parameters) {
        return new PreparedParameterBuilder.Factory() {
            @Override
            protected void fillParameters(PreparedParameterBuilder builder) {
                builder.addDateRange(parameters.getDateRange(), TimeZone.getTimeZone("GMT"))
                        .addId("account", Account.class, parameters.getAccountId());

                List<Long> channelIds = parameters.getChannelIds();
                boolean fetchNames = channelIds != null && channelIds.size() == 1;
                builder.addIds("channel", Channel.class, channelIds, fetchNames);
            }
        };
    }

    private class Report extends AuditableReportSupport {

        private Query urlsQuery;
        private Query pageKeywordsQuery;
        private Query searchKeywordsQuery;
        private Query urlKeywordsQuery;
        private ReportMetaData<DbColumn> urlsMetadata;
        private ReportMetaData<DbColumn> pageKeywordsMetadata;
        private ReportMetaData<DbColumn> searchKeywordsMetadata;
        private ReportMetaData<DbColumn> urlKeywordsMetadata;
        private ChannelTriggersReportParameters parameters;

        public Report(ChannelTriggersReportParameters parameters, AuditResultHandlerWrapper handler) {
            super(handler);
            this.parameters = parameters;
        }

        @Override
        public void execute() {
            try {
                doExecute(urlsQuery, urlsMetadata);
                doExecute(pageKeywordsQuery, pageKeywordsMetadata);
                doExecute(searchKeywordsQuery, searchKeywordsMetadata);
                doExecute(urlKeywordsQuery, urlKeywordsMetadata);
            } finally {
                handler.close();
            }
        }

        @Override
        public void prepare() {
            preparedParameterBuilderFactory = parametersFactory(parameters);
            handler.preparedParameters(preparedParameterBuilderFactory);

            urlsMetadata = prepareMataData(ChannelTriggersMeta.URLS_METADATA.resolve(parameters), parameters.getUrlsSortColumn(), ChannelTriggersMeta.HITS, Order.DESC);
            pageKeywordsMetadata = prepareMataData(ChannelTriggersMeta.PAGE_KEYWORDS_METADATA.resolve(parameters), parameters.getPageKeywordsSortColumn(), ChannelTriggersMeta.HITS, Order.DESC);
            searchKeywordsMetadata = prepareMataData(ChannelTriggersMeta.SEARCH_KEYWORDS_METADATA.resolve(parameters), parameters.getSearchKeywordsSortColumn(), ChannelTriggersMeta.HITS, Order.DESC);
            urlKeywordsMetadata = prepareMataData(ChannelTriggersMeta.URL_KEYWORDS_METADATA.resolve(parameters), parameters.getUrlKeywordsSortColumn(), ChannelTriggersMeta.HITS, Order.DESC);

            if (parameters.isNeedUrls()) {
                urlsQuery = queryFunction(TriggerType.URL, urlsMetadata);
            }
            if (parameters.isNeedPageKeywords()) {
                pageKeywordsQuery = queryFunction(TriggerType.PAGE_KEYWORD, pageKeywordsMetadata);
            }
            if (parameters.isNeedSearchKeywords()) {
                searchKeywordsQuery = queryFunction(TriggerType.SEARCH_KEYWORD, searchKeywordsMetadata);
            }
            if (parameters.isNeedUrlKeywords()) {
                urlKeywordsQuery = queryFunction(TriggerType.URL_KEYWORD, urlKeywordsMetadata);
            }
        }

        private ReportMetaData<DbColumn> prepareMataData(ReportMetaData<DbColumn> metaData, ColumnOrderTO columnOrderTO, DbColumn defaultColumnOrder, Order defaultOrder) {
            ColumnOrder<DbColumn> columnOrder;

            if (columnOrderTO == null) {
                columnOrder = new ColumnOrder<>(defaultColumnOrder, defaultOrder);
            } else {
                columnOrder = new ColumnOrder<>(metaData.find(columnOrderTO.getColumn()), columnOrderTO.getOrder());
            }

            metaData = metaData.order(columnOrder);

            if (parameters.getChannelIds().size() == 1) {
                metaData = metaData.exclude(ChannelTriggersMeta.CHANNEL);
            }

            return metaData;
        }

        private Query queryFunction(TriggerType triggerType, ReportMetaData<DbColumn> metaData) {
            ColumnOrder<DbColumn> columnOrder = metaData.getSortColumns().get(0);
            Map<String,String> replacements = new HashMap<String, String>();
            replacements.put("\\$orderBy", "order by " + columnOrder.getColumn().getResultSetName() + " " + columnOrder.getOrder().name());
            replacements.put("\\$channelIds", StringUtils.join(parameters.getChannelIds(), ","));
            return impalaDb.query(impalaDb.createFunctionTemplate("channel_triggers", replacements).build())
                    .parameter("triggerType", triggerType.getLetter(), Types.VARCHAR)
                    .parameter("dateFrom", parameters.getDateRange().getBegin(), Types.VARCHAR)
                    .parameter("dateTo", parameters.getDateRange().getEnd(), Types.VARCHAR);
        }

        private void doExecute(Query query, ReportMetaData metaData) {
            if (query != null) {
                query.execute(handler, new SimpleIterationStrategy(metaData, false));
            } else {
                handler.before(metaData);
                handler.after();
            }
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CHANNEL_TRIGGERS;
        }

        @Override
        public List<DbColumn> getColumns() {
            LinkedHashSet<DbColumn> res = new LinkedHashSet<DbColumn>();

            if (parameters.isNeedUrls()) {
                res.addAll(urlsMetadata.getOutputColumns());
            }

            if (parameters.isNeedPageKeywords()) {
                res.addAll(pageKeywordsMetadata.getOutputColumns());
            }

            if (parameters.isNeedSearchKeywords()) {
                res.addAll(searchKeywordsMetadata.getOutputColumns());
            }
            if (parameters.isNeedUrlKeywords()) {
                res.addAll(urlKeywordsMetadata.getOutputColumns());
            }
            if (parameters.isNeedUrls()) {
                res.addAll(urlsMetadata.getMetricsColumns());
            }

            if (parameters.isNeedPageKeywords()) {
                res.addAll(pageKeywordsMetadata.getMetricsColumns());
            }

            if (parameters.isNeedSearchKeywords()) {
                res.addAll(searchKeywordsMetadata.getMetricsColumns());
            }
            if (parameters.isNeedUrlKeywords()) {
                res.addAll(urlKeywordsMetadata.getMetricsColumns());
            }

            return new ArrayList<>(res);
        }
    }

    private static class UrlTriggerFormatter extends UrlValueFormatter {

        private ValueFormatter<String> textFormatter;

        private UrlTriggerFormatter(ValueFormatter<String> textFormatter) {
            this.textFormatter = textFormatter;
        }

        @Override
        protected String prepareUrl(String value) {
            final String fixed = StringUtil.extractUrlFromTrigger(value, false);
            return super.prepareUrl(fixed);
        }

        @Override
        protected String prepareTextHtml(String value, FormatterContext context) {
            HtmlCell cell = new HtmlCell();
            textFormatter.formatHtml(cell, value, context);
            return cell.getHtml();
        }
    }
}
