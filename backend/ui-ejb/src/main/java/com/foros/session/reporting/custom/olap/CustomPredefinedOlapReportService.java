package com.foros.session.reporting.custom.olap;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.creative.CreativeSize;
import com.foros.model.isp.Colocation;
import com.foros.model.site.Site;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnOrder;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.EntityUrlLocalisableValueFormatter;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.MultiCurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.NAValueFormatter;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.OneCurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.PercentValueFormatter;
import com.foros.reporting.serializer.formatter.SiteRateValueFormatter;
import com.foros.reporting.serializer.formatter.UserStatusValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.olap.query.OlapQuery;
import com.foros.reporting.tools.olap.query.OlapQueryProvider;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.country.CountryService;
import com.foros.session.reporting.CommonAuditableOlapReportSupport;
import com.foros.session.reporting.OutputType;
import com.foros.session.reporting.PreparedParameter;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.session.reporting.SummarySerializerWrapper;
import com.foros.session.reporting.custom.CustomReportParameters;
import com.foros.session.reporting.parameters.ColumnOrderTO;
import com.foros.session.reporting.parameters.Order;
import com.foros.util.i18n.LocalizableNameProvider;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.util.ValidationUtil;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.commons.lang.StringUtils;

@LocalBean
@Stateless(name = "CustomPredefinedOlapReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class CustomPredefinedOlapReportService {
    private static final Logger logger = Logger.getLogger(CustomPredefinedOlapReportService.class.getName());

    // url patterns
    private static final String AGENCY_URL_PATTERN = "/admin/account/view.action?id=%d";
    private static final String PUBLISHER_URL_PATTERN = "/admin/site/main.action?accountId=%d";
    private static final String ADVERTISER_URL_PATTERN = "/admin/account/view.action?id=%d";
    private static final String CAMPAIGN_URL_PATTERN = "/admin/campaign/view.action?id=%d";
    private static final String CC_URL_PATTERN = "/admin/campaign/group/creative/view.action?id=%d";
    private static final String TAG_URL_PATTERN = "/admin/tag/view.action?id=%d";
    private static final String CCG_URL_PATTERN = "/admin/campaign/group/view.action?id=%d";
    private static final String SIZE_TYPE_URL_PATTERN = "/admin/SizeType/view.action?id=%d";
    private static final String SIZE_URL_PATTERN = "/admin/CreativeSize/view.action?id=%d";
    private static final String SITE_URL_PATTERN = "/admin/site/view.action?id=%d";
    private static final String COLOCATION_URL_PATTERN = "/admin/colocation/view.action?id=%d";
    private static final String DEVICE_CHANNEL_URL_PATTERN = "/admin/DeviceChannel/view.action?id=%d";
    private static final String ISP_PATTERN = "/admin/isp/report/main.action?account.id=%d";
    private static final String CHANNEL_TARGET_PATTERN = "/admin/channel/view.action?id=%d";

    @EJB
    private ReportsService reportsService;

    @EJB
    private OlapQueryProvider queryProvider;

    @EJB
    private ConfigService configService;

    @EJB
    private CountryService countryService;

    @PostConstruct
    public void init() {
        initSemaphore(configService.get(ConfigParameters.MAX_SIMULTANEOUS_CUSTOM_REPORTS));
    }

    @Restrict(restriction = "Report.run", parameters = "'custom'")
    @Validate(validation = "Reporting.custom", parameters = "#parameters")
    public void processExcel(CustomReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os), false);
    }

    @Restrict(restriction = "Report.run", parameters = "'custom'")
    @Validate(validation = "Reporting.custom", parameters = "#parameters")
    public void processCsv(CustomReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os), false);
    }

    @Restrict(restriction = "Report.run", parameters = "'custom'")
    @Validate(validation = "Reporting.custom", parameters = "#parameters")
    public void processHtml(CustomReportParameters parameters, SimpleReportData data, boolean generateSummary) {
        run(parameters, reportsService.createHtmlSerializer(data), generateSummary);
    }

    private void run(CustomReportParameters parameters, AuditResultHandlerWrapper handler, boolean summary) {
        if (!available.tryAcquire()) {
            logger.log(Level.INFO, "Max count of simultaneously running custom reports reached.");
            ValidationContext validationContext = ValidationUtil.createContext();
            validationContext.addConstraintViolation("error.report.custom.maxCountReached");
            validationContext.throwIfHasViolations();
        }

        try {
            reportsService.execute(new Report(parameters, handler, summary));
        } finally {
            available.release();
        }
    }

    public List<PreparedParameter> prepareParameters(CustomReportParameters parameters) {
        return newParametersFactory(parameters)
                .builder()
                .parameters();
    }

    private PreparedParameterBuilder.Factory newParametersFactory(final CustomReportParameters parameters) {
        return new PreparedParameterBuilder.Factory() {
            @Override
            protected void fillParameters(PreparedParameterBuilder builder) {
                String timeZone = (StringUtils.isNotBlank(parameters.getCountryCode())) ?
                        countryService.find(parameters.getCountryCode()).getTimezone().getKey() : "";

                builder.addDateRange(parameters.getDateRange(), timeZone)
                        .addId("agency", AgencyAccount.class, parameters.getAgencyId())
                        .addId("advertiser", AdvertiserAccount.class, parameters.getAdvertiserId())
                        .addId("campaign", Campaign.class, parameters.getCampaignId())
                        .addCountry("publisherCountry", parameters.getCountryCode())
                        .add("CCID", parameters.getCampaignCreativeId())
                        .addId("ISP", IspAccount.class, parameters.getIspId())
                        .addId("colocation", Colocation.class, parameters.getColocationId())
                        .addId("publisher", PublisherAccount.class, parameters.getPublisherId())
                        .addId("site", Site.class, parameters.getSiteId())
                        .addId("creativeSize", CreativeSize.class, parameters.getSizeId())
                        .add("detailLevel", null, parameters.getDetailLevel());
            }
        };
    }

    @SuppressWarnings({"EjbClassBasicInspection"})
    private ValueFormatterRegistryImpl createDefaultRegistry(boolean withLinks) {
        ValueFormatterRegistryImpl registry = ValueFormatterRegistries.registry();
        registry
                .type(ColumnTypes.currency(), new CurrencyValueFormatter("USD"))
                .column(CustomOlapMeta.LIFE_IMPRESSIONS, new NAValueFormatter(new NumberValueFormatter()))
                .column(CustomOlapMeta.LIFE_CLICKS, new NAValueFormatter(new NumberValueFormatter()))
                .column(CustomOlapMeta.LIFE_CTR, new NAValueFormatter(new PercentValueFormatter()))
                .column(CustomOlapMeta.LIFE_ACTIONS, new NAValueFormatter(new NumberValueFormatter()))
                .column(CustomOlapMeta.LIFE_ACTION_RATE, new NAValueFormatter(new PercentValueFormatter()))
                .column(CustomOlapMeta.RATE_GROSS, new MultiCurrencyValueFormatter(CustomOlapMeta.ADV_CURRENCY))
                .column(CustomOlapMeta.RATE_NET, new MultiCurrencyValueFormatter(CustomOlapMeta.ADV_CURRENCY))
                .column(CustomOlapMeta.USER_STATUS_CODE, new UserStatusValueFormatter())
                .column(CustomOlapMeta.REQUESTS_PER_PAGE, new NAValueFormatter(new NumberValueFormatter(1)))
                .column(CustomOlapMeta.IMPRESSIONS_PER_PAGE, new NAValueFormatter(new NumberValueFormatter(1)))
                .column(CustomOlapMeta.TAG_PRICING, new NAValueFormatter(
                        new SiteRateValueFormatter(
                                CustomOlapMeta.PUBLISHER_CURRENCY, CustomOlapMeta.TAG_PRICING_CCG_RATE_TYPE,
                                CustomOlapMeta.TAG_PRICING_CCG_TYPE, CustomOlapMeta.TAG_PRICING_SITE_RATE_TYPE)
                        )
                )
                .columns(new NAValueFormatter(new NumberValueFormatter()), CustomOlapMeta.UNIQUE_USERS_COLUMNS);

        if (withLinks) {
            EntityUrlValueFormatter.Factory uff = new EntityUrlValueFormatter.Factory();
            registry
                    .column(CustomOlapMeta.AGENCY, uff.all(CustomOlapMeta.AGENCY_ID, AGENCY_URL_PATTERN))
                    .column(CustomOlapMeta.PUBLISHER_ACCOUNT, uff.all(CustomOlapMeta.PUBLISHER_ACCOUNT_ID, PUBLISHER_URL_PATTERN))
                    .column(CustomOlapMeta.ADVERTISER, uff.all(CustomOlapMeta.ADVERTISER_ID, ADVERTISER_URL_PATTERN))
                    .column(CustomOlapMeta.CAMPAIGN, uff.all(CustomOlapMeta.CAMPAIGN_ID, CAMPAIGN_URL_PATTERN))
                    .column(CustomOlapMeta.CREATIVE_NAME, uff.all(CustomOlapMeta.CC_ID, CC_URL_PATTERN))
                    .column(CustomOlapMeta.TAG, uff.all(CustomOlapMeta.TAG_ID, TAG_URL_PATTERN))
                    .column(CustomOlapMeta.CREATIVE_GROUP, uff.all(CustomOlapMeta.CREATIVE_GROUP_ID, CCG_URL_PATTERN))
                    .column(CustomOlapMeta.SIZE_NAME, uff.all(CustomOlapMeta.SIZE_ID, SIZE_URL_PATTERN))
                    .column(CustomOlapMeta.SITE, uff.all(CustomOlapMeta.SITE_ID, SITE_URL_PATTERN))
                    .column(CustomOlapMeta.COLOCATION, uff.all(CustomOlapMeta.COLOCATION_ID, COLOCATION_URL_PATTERN))
                    .column(CustomOlapMeta.CHANNEL_DEVICE_NAME, uff.all(CustomOlapMeta.CHANNEL_DEVICE_ID, DEVICE_CHANNEL_URL_PATTERN))
                    .column(CustomOlapMeta.ISP_ACCOUNT, uff.all(CustomOlapMeta.ISP_ACCOUNT_ID, ISP_PATTERN))
                    .column(CustomOlapMeta.CHANNEL_TARGET_NAME, uff.all(CustomOlapMeta.CHANNEL_TARGET_ID, CHANNEL_TARGET_PATTERN))
                    .column(CustomOlapMeta.SIZE_TYPE, new EntityUrlLocalisableValueFormatter(LocalizableNameProvider.SIZE_TYPE, CustomOlapMeta.SIZE_TYPE_ID, SIZE_TYPE_URL_PATTERN));
        }

        return registry;
    }

    private class Report extends CommonAuditableOlapReportSupport<CustomReportParameters> {
        private final boolean executeSummary;

        private Report(CustomReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler);
            this.executeSummary = executeSummary;
            this.metaData = CustomOlapMeta.INSTANCE.resolve(parameters);
        }

        @Override
        protected OlapQuery buildQuery() {
            Set<OlapColumn> metrics = metaData.getMetricsColumnsMeta().getColumnsWithDependencies();
            Set<OlapColumn> output = metaData.getOutputColumnsMeta().getColumnsWithDependencies();

            Long agencyId;
            if (parameters.getAgencyId() != null && parameters.getAgencyId() < 0) {
                agencyId = 0L;
            } else {
                agencyId = parameters.getAgencyId();
            }
            return queryProvider
                    .query("CustomReport", parameters)
                    .limit(handler.getMaxRows() + 1)
                    /* columns */
                    .columns(metrics)
                    .columns(output)
                    /* order */
                    .order(buildSortColumns())
                    /* filters */
                    .filter(CustomOlapMeta.Levels.DATE_VALUE, parameters.getDateRange())
                    .filter(CustomOlapMeta.Levels.AGENCY_ID, agencyId)
                    .filter(CustomOlapMeta.Levels.ADVERTISER_ID, parameters.getAdvertiserId())
                    .filter(CustomOlapMeta.Levels.CAMPAIGN_ID, parameters.getCampaignId())
                    .filter(CustomOlapMeta.Levels.CC_ID, parameters.getCampaignCreativeId())
                    .filter(CustomOlapMeta.Levels.PUBLISHER_COUNTRY, parameters.getCountryCode())
                    .filter(CustomOlapMeta.Levels.PUBLISHER_ACCOUNT_ID, parameters.getPublisherId())
                    .filter(CustomOlapMeta.Levels.SITE_ID, parameters.getSiteId())
                    .filter(CustomOlapMeta.Levels.ISP_ACCOUNT_ID, parameters.getIspId())
                    .filter(CustomOlapMeta.Levels.COLO_ID, parameters.getColocationId())
                    .filter(CustomOlapMeta.Levels.SIZE_ID, parameters.getSizeId());
        }

        @Override
        public void prepare() {

            prepareRegistries();

            this.preparedParameterBuilderFactory = newParametersFactory(this.parameters);

            this.handler.preparedParameters(this.preparedParameterBuilderFactory);

            String tagIdName = CustomOlapMeta.TAG_ID.getNameKey();
            String tagPricingName = CustomOlapMeta.TAG_PRICING.getNameKey();
            List<String> parametersOutputColumns = parameters.getOutputColumns();
            if (parametersOutputColumns.contains(tagPricingName) && !parametersOutputColumns.contains(tagIdName)) {
                int index = parametersOutputColumns.indexOf(tagPricingName);
                parametersOutputColumns.add(index, tagIdName);
            }

            this.metaData = CustomOlapMeta.INSTANCE.resolve(parameters).retainById(parametersOutputColumns, parameters.getMetricsColumns());

            if (!parameters.isOutputInAccountCurrency()) {
                this.metaData.replace(CustomOlapMeta.INVENTORY_COST_NET, CustomOlapMeta.INVENTORY_COST_NET_USD);
                this.metaData.replace(CustomOlapMeta.INVENTORY_COST_GROSS, CustomOlapMeta.INVENTORY_COST_GROSS_USD);
                this.metaData.replace(CustomOlapMeta.NET_ECPM, CustomOlapMeta.NET_ECPM_USD);
                this.metaData.replace(CustomOlapMeta.GROSS_ECPM, CustomOlapMeta.GROSS_ECPM_USD);
                this.metaData.replace(CustomOlapMeta.NET_ECPC, CustomOlapMeta.NET_ECPC_USD);
                this.metaData.replace(CustomOlapMeta.GROSS_ECPC, CustomOlapMeta.GROSS_ECPC_USD);
                this.metaData.replace(CustomOlapMeta.NET_ECPA, CustomOlapMeta.NET_ECPA_USD);
                this.metaData.replace(CustomOlapMeta.GROSS_ECPA, CustomOlapMeta.GROSS_ECPA_USD);
            }

            ColumnOrderTO sortColumn = parameters.getSortColumn();

            if (sortColumn != null && !sortColumn.getColumn().isEmpty()) {
                this.metaData = this.metaData.orderById(sortColumn);
            }
        }

        private void prepareRegistries() {
            ValueFormatterRegistry registry = createRegistry();
            handler.registry(registry, RowTypes.data());

            if (executeSummary) {
                handler.registry(registry, RowTypes.summary());
            }
        }

        private ValueFormatterRegistry createRegistry() {
            boolean withLinks = OutputType.EXCEL_NOLINKS != parameters.getOutputType() && OutputType.CSV != parameters.getOutputType();

            ValueFormatterRegistryImpl dataRegistry = createDefaultRegistry(withLinks);

            if (parameters.isOutputInAccountCurrency()) {
                dataRegistry
                        .columns(new OneCurrencyValueFormatter(CustomOlapMeta.ADV_CURRENCY), CustomOlapMeta.ADVERTISER_CURRENCY_COLUMNS)
                        .column(CustomOlapMeta.PAYOUT, new OneCurrencyValueFormatter(CustomOlapMeta.PUBLISHER_CURRENCY));
            }

            return dataRegistry;
        }

        @Override
        protected ResultHandler wrap(ResultSerializer handler) {
            if (executeSummary && needExecuteSummary()) {
                return new SummarySerializerWrapper(handler, metaData.retain(CustomOlapMeta.SUMMARY_COLUMNS));
            }

            return handler;
        }

        private boolean needExecuteSummary() {
            return !metaData.getColumnsMeta().containsAny(CustomOlapMeta.NO_SUMMARY_CRITERIA_COLUMUNS);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CUSTOM;
        }

        private List<ColumnOrder<OlapColumn>> buildSortColumns() {
            Set<ColumnOrder<OlapColumn>> res = new LinkedHashSet<>();

            res.addAll(metaData.getSortColumns());

            Order order;
            if ((order = findContainsAdvCurrencySortColumn()) != null) {
                res.add(new ColumnOrder<>(CustomOlapMeta.ADV_CURRENCY, order));
            }

            if ((order = findContainsRateCurrencySortColumn()) != null) {
                res.add(new ColumnOrder<>(CustomOlapMeta.ADV_CURRENCY, order));
            }

            if ((order = findContainsPubCurrencySortColumn()) != null) {
                res.add(new ColumnOrder<>(CustomOlapMeta.PUBLISHER_CURRENCY, order));
            }

            return new ArrayList<>(res);
        }

        private Order findContainsAdvCurrencySortColumn() {
            return findColumnOrder(metaData.getSortColumns(), CustomOlapMeta.ADVERTISER_CURRENCY_COLUMNS);
        }

        private Order findContainsPubCurrencySortColumn() {
            return findColumnOrder(metaData.getSortColumns(), Collections.singleton(CustomOlapMeta.PAYOUT));
        }

        private Order findContainsRateCurrencySortColumn() {
            return findColumnOrder(metaData.getSortColumns(), CustomOlapMeta.RATE_CURRENCY_COLUMNS);
        }

        private Order findColumnOrder(
                Collection<ColumnOrder<OlapColumn>> columns1,
                Collection<OlapColumn> columns2) {
            for (ColumnOrder<OlapColumn> columnOrder : columns1) {
                if (columns2.contains(columnOrder.getColumn())) {
                    return columnOrder.getOrder();
                }
            }
            return null;
        }
    }

    // concurrency control

    private static Semaphore available;

    private synchronized void initSemaphore(int permits) {
        if (available == null) {
            available = new Semaphore(permits, true);
        }
    }
}
