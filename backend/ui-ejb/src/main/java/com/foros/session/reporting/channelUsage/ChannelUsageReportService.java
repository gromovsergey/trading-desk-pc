package com.foros.session.reporting.channelUsage;

import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.PreparedParameterBuilder;
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
@Stateless(name = "ChannelUsageReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class ChannelUsageReportService implements GenericReportService<ChannelUsageReportParameters, SimpleReportData> {

    @EJB
    private ReportsService reportsService;

    @EJB
    private StatsDbQueryProvider statDb;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    @Restrict(restriction = "Report.run", parameters = "'channelUsage'")
    @Validate(validation = "Reporting.channelUsage", parameters = "#parameters")
    public void processExcel(ChannelUsageReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'channelUsage'")
    @Validate(validation = "Reporting.channelUsage", parameters = "#parameters")
    public void processCsv(ChannelUsageReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'channelUsage'")
    @Validate(validation = "Reporting.channelUsage", parameters = "#parameters")
    public void processHtml(ChannelUsageReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data));
    }

    private void run(ChannelUsageReportParameters parameters, AuditResultHandlerWrapper handler) {
        reportsService.execute(new Report(parameters, handler, true));
    }

    private class Report extends CommonAuditableReportSupport<ChannelUsageReportParameters> {

        public Report(ChannelUsageReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    Account account = em.find(Account.class, parameters.getAccountId());
                    builder.addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone())
                            .addId("channelMarketplaceAccount", Account.class, parameters.getAccountId())
                            .addId("channel", Channel.class, parameters.getChannelId());
                }
            };

            String accountCurrencyCode = em.find(Account.class, parameters.getAccountId()).getCurrency().getCurrencyCode();

            ValueFormatterRegistryImpl registry = ValueFormatterRegistries.registry()
                    .type(ColumnTypes.currency(), new CurrencyValueFormatter(accountCurrencyCode));

            handler.preparedParameters(preparedParameterBuilderFactory);
            handler.registry(registry, RowTypes.data());

            String summaryFunctionName = "report.channel_usage_total";

            String functionName;
            switch (parameters.getDetailLevel()) {
                case date:
                    functionName = "report.channel_usage_by_date";
                    metaData = ChannelUsageMeta.META_BY_DATE.resolve(parameters);
                    break;
                case channel:
                    functionName = "report.channel_usage_by_channel";
                    metaData = ChannelUsageMeta.META_BY_CHANNEL.resolve(parameters);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown detail level for Channel Usage Report");
            }

            summaryMetaData = metaData.metricsOnly();

            query = statDb.queryFunction(functionName)
                    .parameter("p_from_date", parameters.getDateRange().getBegin(), PostgreLocalDateUserType.INSTANCE)
                    .parameter("p_to_date", parameters.getDateRange().getEnd(), PostgreLocalDateUserType.INSTANCE)
                    .parameter("p_account_id", parameters.getAccountId(), Types.INTEGER)
                    .parameter("p_channel_id", parameters.getChannelId(), Types.BIGINT)
                    .parameter("p_max_rows", handler.getMaxRows() + 1);

            summaryQuery = statDb.queryFunction(summaryFunctionName)
                    .parameter("p_from_date", parameters.getDateRange().getBegin(), PostgreLocalDateUserType.INSTANCE)
                    .parameter("p_to_date", parameters.getDateRange().getEnd(), PostgreLocalDateUserType.INSTANCE)
                    .parameter("p_account_id", parameters.getAccountId(), Types.INTEGER)
                    .parameter("p_channel_id", parameters.getChannelId(), Types.BIGINT);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CHANNEL_USAGE;
        }
    }

}
