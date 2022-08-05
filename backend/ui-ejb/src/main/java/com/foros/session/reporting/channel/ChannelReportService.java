package com.foros.session.reporting.channel;

import com.foros.model.account.Account;
import com.foros.model.channel.*;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.currency.Currency;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.meta.ResultSetNameTransformer;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyConverterFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreSimpleArrayUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.CurrentUserService;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.*;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

@LocalBean
@Stateless(name = "ChannelReportService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class ChannelReportService implements GenericReportService<ChannelReportParameters, SimpleReportData> {

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private ReportsService reportsService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private SearchChannelService channelService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    @Restrict(restriction = "Report.Channel.run", parameters = "#parameters")
    @Validate(validation = "Reporting.channel", parameters = "#parameters")
    public void processHtml(ChannelReportParameters parameters, SimpleReportData data) {
        processChannelReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.Channel.run", parameters = "#parameters")
    @Validate(validation = "Reporting.channel", parameters = "#parameters")
    public void processExcel(ChannelReportParameters parameters, OutputStream stream) {
        processChannelReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.Channel.run", parameters = "#parameters")
    @Validate(validation = "Reporting.channel", parameters = "#parameters")
    public void processCsv(ChannelReportParameters parameters, OutputStream stream) {
        processChannelReport(parameters, reportsService.createCsvSerializer(stream));
    }

    public ReportMetaData<DbColumn> getMetaData(Channel channel, boolean isInternal) {
        boolean isBehavioral = channel instanceof BehavioralChannel;
        boolean isDiscover = channel instanceof DiscoverChannel;
        boolean isKeyword = channel instanceof KeywordChannel;
        boolean isBDK = isBehavioral || isDiscover || isKeyword;

        if (!isBDK &&
                !(channel instanceof AudienceChannel) &&
                !(channel instanceof ExpressionChannel) &&
                !(channel instanceof GeoChannel)) {
            return null;
        }

        List<DbColumn> columns = new ArrayList<>(ChannelMeta.META_DATA.getColumns().size());

        columns.addAll(Arrays.asList(ChannelMeta.DATE, ChannelMeta.TOTAL_UNIQUES, ChannelMeta.ACTIVE_DAILY_UNIQUES));

        if (!isDiscover) {
            columns.addAll(Arrays.asList(
                    ChannelMeta.OPPOR_SERVE_IMPS,
                    ChannelMeta.OPPOR_SERVE_UNIQUES));
            if (isInternal) {
                columns.addAll(Arrays.asList(
                    ChannelMeta.OPPOR_SERVE_VALUE,
                    ChannelMeta.OPPOR_SERVE_ECPM));
            }

            columns.addAll(Arrays.asList(
                    ChannelMeta.SERVED_IMPS,
                    ChannelMeta.SERVED_CLICKS,
                    ChannelMeta.SERVED_CTR,
                    ChannelMeta.SERVED_UNIQUES));

            if (isInternal) {
                columns.addAll(Arrays.asList(
                    ChannelMeta.SERVED_VALUE,
                    ChannelMeta.SERVED_ECPM));
            }
                                
            columns.addAll(Arrays.asList(
                    ChannelMeta.NOT_SERVED_FOROS_IMPS,
                    ChannelMeta.NOT_SERVED_FOROS_UNIQUES));
                    
            if (isInternal) {
                columns.addAll(Arrays.asList(
                    ChannelMeta.NOT_SERVED_FOROS_VALUE,
                    ChannelMeta.NOT_SERVED_FOROS_ECPM));
            }

            if (isInternal) {
                columns.addAll(Arrays.asList(
                        ChannelMeta.NOT_SERVED_NO_FOROS_IMPS,
                        ChannelMeta.NOT_SERVED_NO_FOROS_UNIQUES,
                        ChannelMeta.NOT_SERVED_NO_FOROS_ECPM,
                        ChannelMeta.NOT_SERVED_NO_FOROS_VALUE));
            }
        }

        if (isBDK) {
            if (!isKeyword) {
                columns.add(ChannelMeta.MATCHED_URL);
            }
            columns.add(ChannelMeta.SEARCH_KEYWORDS);
            columns.add(ChannelMeta.MATCHED_KEYWORDS);
            if (isBehavioral) {
                columns.add(ChannelMeta.MATCHED_URL_KEYWORDS);
            }
            columns.add(ChannelMeta.TOTAL_MATCH);
        }

        return ChannelMeta.META_DATA.retain(columns);
    }

    private void processChannelReport(ChannelReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer, false));
    }

    private class Report extends CommonAuditableReportSupport<ChannelReportParameters> {

        public Report(ChannelReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            final boolean isInternal = currentUserService.isInternal();
            final Channel channel = channelService.find(parameters.getChannelId());

            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    builder.addDateRange(parameters.getDateRange(), TimeZone.getTimeZone("GMT"));
                    if (!(channel instanceof GeoChannel)) {
                        builder.addId("account", Account.class, channel.getAccount().getId());
                    }
                    builder.addId("channel", Channel.class, parameters.getChannelId());
                }
            };
            handler.preparedParameters(preparedParameterBuilderFactory);

            ValueFormatterRegistryImpl registry = ValueFormatterRegistries.registry()
                    .type(ColumnTypes.currency(), new CurrencyConverterFormatter(getCurrencyCode(channel, isInternal)));
            handler.registry(registry, RowTypes.data());

            metaData = getMetaData(channel, isInternal).retainById(parameters.getOutputCols(), parameters.getMetricCols());

            query = statsDb.queryCallable("report.channel_report")
                    .parameter("fromDate", parameters.getDateRange().getBegin(), PostgreLocalDateUserType.INSTANCE)
                    .parameter("toDate", parameters.getDateRange().getEnd(), PostgreLocalDateUserType.INSTANCE)
                    .parameter("output_cols", ResultSetNameTransformer.getResultSetNames(metaData.getOutputColumnsMeta().getColumnsWithDependencies()), PostgreSimpleArrayUserType.INSTANCE)
                    .parameter("metric_cols", ResultSetNameTransformer.getResultSetNames(metaData.getMetricsColumnsMeta().getColumnsWithDependencies()), PostgreSimpleArrayUserType.INSTANCE)
                    .parameter("channelId", parameters.getChannelId(), Types.BIGINT)
                    .parameter("isExternal", !isInternal, Types.BOOLEAN);
        }

        private String getCurrencyCode(Channel channel, boolean isInternal) {
            String currencyCode;

            if (isInternal) {
                if (channel instanceof GeoChannel) {
                    Currency currency = channel.getCountry().getCurrency();
                    if (currency != null) {
                        currencyCode = currency.getCurrencyCode();
                    } else {
                        currencyCode = em.find(Account.class, currentUserService.getAccountId()).getCurrency().getCurrencyCode();
                    }
                } else {
                    currencyCode = em.find(Account.class, channel.getAccount().getId()).getCurrency().getCurrencyCode();
                }
            } else {
                currencyCode = em.find(Account.class, currentUserService.getAccountId()).getCurrency().getCurrencyCode();
            }

            return currencyCode;
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CHANNEL;
        }
    }
}
