package com.foros.session.reporting.publisher;

import com.foros.model.account.Account;
import com.foros.model.account.PublisherAccount;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnOrder;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CountryValueFormatter;
import com.foros.reporting.serializer.formatter.MultiCurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.NAValueFormatter;
import com.foros.reporting.serializer.formatter.PercentValueFormatter;
import com.foros.reporting.serializer.formatter.TagRateValueFormatter;
import com.foros.reporting.serializer.formatter.UserStatusValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.olap.query.OlapQuery;
import com.foros.reporting.tools.olap.query.OlapQueryProvider;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.account.AccountService;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.reporting.CommonAuditableOlapReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.session.reporting.SummarySerializerWrapper;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.session.reporting.parameters.Order;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless(name = "PublisherReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class PublisherReportService implements GenericReportService<PublisherReportParameters, SimpleReportData> {
    @EJB
    private ReportsService reportsService;

    @EJB
    private WalledGardenService walledGardenService;

    @EJB
    private OlapQueryProvider queryProvider;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @Override
    @Restrict(restriction = "Report.Publisher.run", parameters = "#parameters")
    @Validate(validation = "Reporting.publisher", parameters = "#parameters")
    public void processExcel(PublisherReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os), false);
    }

    @Override
    @Restrict(restriction = "Report.Publisher.run", parameters = "#parameters")
    @Validate(validation = "Reporting.publisher", parameters = "#parameters")
    public void processCsv(PublisherReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os), false);
    }

    @Override
    @Restrict(restriction = "Report.Publisher.run", parameters = "#parameters")
    @Validate(validation = "Reporting.publisher", parameters = "#parameters")
    public void processHtml(PublisherReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data), true);
    }

    private void run(PublisherReportParameters parameters, AuditResultHandlerWrapper handler, boolean summary) {
        reportsService.execute(new Report(parameters, handler, summary));
    }

    private PreparedParameterBuilder.Factory newParametersFactory(final PublisherReportParameters parameters) {
        return new PreparedParameterBuilder.Factory() {
            @Override
            protected void fillParameters(PreparedParameterBuilder builder) {
                PublisherAccount account = em.find(PublisherAccount.class, parameters.getAccountId());
                builder.addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone())
                        .addId("account", Account.class, parameters.getAccountId())
                        .addId("site", Site.class, parameters.getSiteId())
                        .addId("tag", Tag.class, parameters.getTagId())
                        .addCountry(parameters.getCountryCode());
            }
        };
    }

    private class Report extends CommonAuditableOlapReportSupport<PublisherReportParameters> {
        private final boolean executeSummary;

        public Report(PublisherReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler);
            this.executeSummary = executeSummary;
            this.metaData = PublisherMeta.ALL.resolve(parameters);
        }

        @Override
        protected OlapQuery buildQuery() {
            Set<OlapColumn> metrics = metaData.getMetricsColumnsMeta().getColumnsWithDependencies();
            Set<OlapColumn> output = metaData.getOutputColumnsMeta().getColumnsWithDependencies();

            return queryProvider
                    .query("PublisherDailyStats", parameters)
                    .limit(handler.getMaxRows() + 1)
                    /* columns */
                    .columns(metrics)
                    .columns(output)
                    /* order */
                    .order(buildSortColumns())
                    /* filters */
                    .filter(PublisherMeta.Levels.DATE_VALUE, parameters.getDateRange())
                    .filter(PublisherMeta.Levels.PUBLISHER_ACCOUNT_ID, parameters.getAccountId())
                    .filter(PublisherMeta.Levels.SITE_ID, parameters.getSiteId())
                    .filter(PublisherMeta.Levels.TAG_ID, parameters.getTagId())
                    .filter(PublisherMeta.Levels.USER_COUNTRY_CODE, parameters.getCountryCode());
        }

        @Override
        public void prepare() {
            prepareRegistries();

            this.preparedParameterBuilderFactory = newParametersFactory(this.parameters);
            this.handler.preparedParameters(this.preparedParameterBuilderFactory);

            prepareMetaData();
        }

        private void prepareMetaData() {
            boolean isWalledGarden = walledGardenService.isPublisherWalledGarden(parameters.getAccountId());
            if (isWalledGarden) {
                metaData = PublisherMeta.ALL_WG.resolve(parameters).retainById(parameters.getColumns());
            } else {
                metaData = PublisherMeta.ALL_NON_WG.resolve(parameters).retainById(parameters.getColumns());
            }

            if (currentUserService.isExternal()) {
                PublisherAccount publisher = accountService.findPublisherAccount(currentUserService.getAccountId());
                boolean hideClicksData = !publisher.getAccountType().isClicksDataVisibleToExternal();
                if (hideClicksData) {
                    metaData = metaData.exclude(PublisherMeta.CLICKS_DATA);
                }
                metaData = metaData.exclude(PublisherMeta.CREDITED_IMPRESSIONS);
            }
        }

        protected ResultHandler wrap(ResultSerializer handler) {
            if (executeSummary) {
                return new SummarySerializerWrapper(handler, metaData.metricsOnly());
            }

            return handler;
        }

        private ValueFormatterRegistryImpl createSummaryRegistry() {
            ValueFormatterRegistryImpl summaryRegistry = ValueFormatterRegistries.registry()
                    .column(PublisherMeta.REVENUE, new MultiCurrencyValueFormatter(PublisherMeta.PUBLISHER_CURRENCY))
                    .column(PublisherMeta.REVENUE_FOROS, new MultiCurrencyValueFormatter(PublisherMeta.PUBLISHER_CURRENCY))
                    .column(PublisherMeta.REVENUE_WG, new MultiCurrencyValueFormatter(PublisherMeta.PUBLISHER_CURRENCY))
                    .column(PublisherMeta.ECPM, new MultiCurrencyValueFormatter(PublisherMeta.PUBLISHER_CURRENCY))
                    .column(PublisherMeta.COUNTRY, new UserStatusValueFormatter())
                    .column(PublisherMeta.CTR, new PercentValueFormatter());
            return summaryRegistry;
        }

        private void prepareRegistries() {
            handler.registry(createRegistry(), RowTypes.data());

            if (executeSummary) {
                handler.registry(createSummaryRegistry(), RowTypes.summary());
            }
        }

        private ValueFormatterRegistry createRegistry() {
            ValueFormatterRegistryImpl dataRegistry = ValueFormatterRegistries.registry()
                    .column(PublisherMeta.COUNTRY, new CountryValueFormatter())
                    .column(PublisherMeta.REVENUE, new MultiCurrencyValueFormatter(PublisherMeta.PUBLISHER_CURRENCY))
                    .column(PublisherMeta.REVENUE_FOROS, new MultiCurrencyValueFormatter(PublisherMeta.PUBLISHER_CURRENCY))
                    .column(PublisherMeta.REVENUE_WG, new MultiCurrencyValueFormatter(PublisherMeta.PUBLISHER_CURRENCY))
                    .column(PublisherMeta.ECPM, new MultiCurrencyValueFormatter(PublisherMeta.PUBLISHER_CURRENCY))
                    .column(PublisherMeta.TAG_PRICING, new NAValueFormatter(
                            new TagRateValueFormatter(
                                    PublisherMeta.PUBLISHER_CURRENCY,
                                    PublisherMeta.TAG_PRICING_COUNTRY_CODE,
                                    PublisherMeta.TAG_PRICING_CCG_RATE_TYPE,
                                    PublisherMeta.TAG_PRICING_CCG_TYPE,
                                    PublisherMeta.TAG_PRICING_SITE_RATE_TYPE)))
                    .column(PublisherMeta.CTR, new PercentValueFormatter());
            return dataRegistry;
        }

        private List<ColumnOrder<OlapColumn>> buildSortColumns() {
            Set<ColumnOrder<OlapColumn>> res = new LinkedHashSet<>();
            for (OlapColumn column : metaData.getOutputColumns()) {
                res.add(new ColumnOrder<OlapColumn>(column, Order.ASC));
            }
            return new ArrayList<>(res);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.PUBLISHER;
        }
    }

    @Restrict(restriction = "Report.Publisher.run", parameters = "#accountId")
    public boolean checkCreditedImps(DateRange dateRange, Long accountId) {
        return jdbcTemplate.queryForObject(
                "select * from statqueries.pub_credited_imps_exists(?::date, ?::date, ?::int)",
                Boolean.class,
                dateRange.getBegin(), dateRange.getEnd(), accountId);
    }
}
