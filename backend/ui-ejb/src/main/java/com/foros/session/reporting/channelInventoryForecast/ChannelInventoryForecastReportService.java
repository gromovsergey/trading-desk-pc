package com.foros.session.reporting.channelInventoryForecast;

import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.creative.CreativeSize;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyConverterFormatter;
import com.foros.reporting.serializer.formatter.DisplayStatusValueFormatter;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.LocalizableNameValueFormatter;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.parameters.usertype.PostgreIntArrayUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLongArrayUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportParameters.DateRangeFilter;
import com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportParameters.DetailLevelType;
import com.foros.util.StringUtil;
import com.foros.util.i18n.LocalizableNameProvider;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.sql.Types;
import java.util.Collection;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.usertype.UserType;
import org.joda.time.LocalDate;

@LocalBean
@Stateless(name = "ChannelInventoryForecastReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class ChannelInventoryForecastReportService
        implements GenericReportService<ChannelInventoryForecastReportParameters, SimpleReportData> {

    private static final UserType IDS_TYPE = new PostgreLongArrayUserType();

    private static final UserType INT_IDS_TYPE = new PostgreIntArrayUserType();

    private static final String CHANNEL_URL_PATTERN = "../../channel/view.action?id=%d";

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private ReportsService reportsService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CurrencyExchangeService currencyExchangeService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    @Restrict(restriction = "Report.ChannelInventory.run", parameters = "#parameters")
    @Validate(validation = "Reporting.channelInventory", parameters = "#parameters")
    public void processHtml(ChannelInventoryForecastReportParameters parameters, SimpleReportData data) {
        processChannelInventoryForecast(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.ChannelInventory.run", parameters = "#parameters")
    @Validate(validation = "Reporting.channelInventory", parameters = "#parameters")
    public void processExcel(ChannelInventoryForecastReportParameters parameters, OutputStream stream) {
        processChannelInventoryForecast(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.ChannelInventory.run", parameters = "#parameters")
    @Validate(validation = "Reporting.channelInventory", parameters = "#parameters")
    public void processCsv(ChannelInventoryForecastReportParameters parameters, OutputStream stream) {
        processChannelInventoryForecast(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processChannelInventoryForecast(ChannelInventoryForecastReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer, false));
    }

    private class Report extends CommonAuditableReportSupport<ChannelInventoryForecastReportParameters> {
        public Report(ChannelInventoryForecastReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            final Collection<Long> channelIds = parameters.getChannelIds();
            final boolean singleChannelMode = channelIds.size() == 1;

            ValueFormatterRegistryImpl registry = ValueFormatterRegistries.registry()
                    .column(ChannelInventoryForecastMeta.CHANNEL, EntityUrlValueFormatter.html(ChannelInventoryForecastMeta.CHANNEL_ID, CHANNEL_URL_PATTERN))
                    .column(ChannelInventoryForecastMeta.CHANNEL_STATUS, new DisplayStatusValueFormatter(Channel.displayStatusMap))
                    .column(ChannelInventoryForecastMeta.SIZE_NAME, new LocalizableNameValueFormatter(LocalizableNameProvider.CREATIVE_SIZE, ChannelInventoryForecastMeta.SIZE_ID))
                    .column(ChannelInventoryForecastMeta.CPM, getCPMFormatter())
                    .column(ChannelInventoryForecastMeta.IMPRESSIONS, new NumberValueFormatter())
                    .column(ChannelInventoryForecastMeta.DAILY_UNIQUE_USERS, new NumberValueFormatter());

            handler.registry(registry, RowTypes.data());
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    builder
                            .add("dateRange", StringUtil.getLocalizedString(parameters.getDateRange().getKey()))
                            .addId("channelCreatorAccount", Account.class, parameters.getAccountId())
                            .add("filter", StringUtil.getLocalizedString(parameters.getChannelFilter().getKey()))
                            .addIds("channels", Channel.class, channelIds)
                            .addIds("creativeSizes", CreativeSize.class, parameters.getCreativeSizeIds())
                            .add("detailLevel", parameters.getDetailLevelType().equals(DetailLevelType.FULL) ? StringUtil.getLocalizedString("channel.inventoryForecast.fullDetailLevel") : parameters.getPercentile() + "%");
                }
            };

            query = statsDb.queryFunction("report.channel_inventory")
                    .parameter("account_id", parameters.getAccountId(), Types.INTEGER)
                    .parameter("channel_filter", parameters.getChannelFilter().name(), Types.VARCHAR)
                    .parameter("channel_ids", channelIds.isEmpty() ? null : channelIds, IDS_TYPE)
                    .parameter("size_ids", parameters.getCreativeSizeIds(), INT_IDS_TYPE)
                    .parameter("date", DateRangeFilter.DR_30_DAYS_AVERAGE.equals(parameters.getDateRange()) ? null : new LocalDate().minusDays(1), Types.DATE)
                    .parameter("percentile", DetailLevelType.FULL.equals(parameters.getDetailLevelType()) ? null : (parameters.getPercentile() / 100f), Types.NUMERIC);

            metaData = singleChannelMode ?
                    ChannelInventoryForecastMeta.SINGLE_CHANNEL_META.resolve(parameters) :
                    ChannelInventoryForecastMeta.META.resolve(parameters);

            handler.preparedParameters(preparedParameterBuilderFactory);
        }

        private ValueFormatter<Number> getCPMFormatter() {
            final String currencyCode;
            if (parameters.getTargetCurrencyCode() == null) {
                currencyCode = em.find(Account.class, parameters.getAccountId()).getCurrency().getCurrencyCode();
            } else {
                currencyCode = parameters.getTargetCurrencyCode();
            }
            return new CurrencyConverterFormatter(currencyCode);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CHANNEL_INVENTORY;
        }
    }
}
