package com.foros.session.reporting.conversions;

import static com.foros.session.reporting.conversions.ConversionsMeta.ADVERTISER;
import static com.foros.session.reporting.conversions.ConversionsMeta.ADVERTISER_ID;
import static com.foros.session.reporting.conversions.ConversionsMeta.ADVERTISER_URL_PATTERN;
import static com.foros.session.reporting.conversions.ConversionsMeta.ADVERTISER_VISIBLE;
import static com.foros.session.reporting.conversions.ConversionsMeta.CAMPAIGN;
import static com.foros.session.reporting.conversions.ConversionsMeta.CAMPAIGN_ID;
import static com.foros.session.reporting.conversions.ConversionsMeta.CAMPAIGN_URL_PATTERN;
import static com.foros.session.reporting.conversions.ConversionsMeta.CAMPAIGN_VISIBLE;
import static com.foros.session.reporting.conversions.ConversionsMeta.CHANNEL;
import static com.foros.session.reporting.conversions.ConversionsMeta.CHANNEL_ACCOUNT_ROLE_ID;
import static com.foros.session.reporting.conversions.ConversionsMeta.CHANNEL_ID;
import static com.foros.session.reporting.conversions.ConversionsMeta.CHANNEL_URL_PATTERN;
import static com.foros.session.reporting.conversions.ConversionsMeta.CHANNEL_VISIBLE;
import static com.foros.session.reporting.conversions.ConversionsMeta.CONVERSION;
import static com.foros.session.reporting.conversions.ConversionsMeta.CONVERSION_CATEGORY;
import static com.foros.session.reporting.conversions.ConversionsMeta.CONVERSION_ID;
import static com.foros.session.reporting.conversions.ConversionsMeta.CONVERSION_URL_PATTERN;
import static com.foros.session.reporting.conversions.ConversionsMeta.CONVERSION_VISIBLE;
import static com.foros.session.reporting.conversions.ConversionsMeta.COST;
import static com.foros.session.reporting.conversions.ConversionsMeta.CREATIVE;
import static com.foros.session.reporting.conversions.ConversionsMeta.CREATIVE_GROUP;
import static com.foros.session.reporting.conversions.ConversionsMeta.CREATIVE_GROUP_ID;
import static com.foros.session.reporting.conversions.ConversionsMeta.CREATIVE_GROUP_URL_PATTERN;
import static com.foros.session.reporting.conversions.ConversionsMeta.CREATIVE_GROUP_VISIBLE;
import static com.foros.session.reporting.conversions.ConversionsMeta.CREATIVE_ID;
import static com.foros.session.reporting.conversions.ConversionsMeta.CREATIVE_URL_PATTERN;
import static com.foros.session.reporting.conversions.ConversionsMeta.CREATIVE_VISIBLE;
import static com.foros.session.reporting.conversions.ConversionsMeta.EX_ADVERTISER_URL_PATTERN;
import static com.foros.session.reporting.conversions.ConversionsMeta.PUBLISHER;
import static com.foros.session.reporting.conversions.ConversionsMeta.PUBLISHER_ID;
import static com.foros.session.reporting.conversions.ConversionsMeta.PUBLISHER_URL_PATTERN;
import static com.foros.session.reporting.conversions.ConversionsMeta.PUBLISHER_VISIBLE;
import static com.foros.session.reporting.conversions.ConversionsMeta.REVENUE;
import static com.foros.session.reporting.conversions.ConversionsMeta.ROI;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.model.action.ConversionCategory;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResultSetNameTransformer;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.ConditionEntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.ConditionEntityUrlValueFormatter.Condition;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.NAValueFormatter;
import com.foros.reporting.serializer.formatter.PercentValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.parameters.usertype.PostgreIntArrayUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreSimpleArrayUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.account.AgencyAdvertiserAccountRestrictions;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless(name = "ConversionsReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class ConversionsReportService implements GenericReportService<ConversionsReportParameters, SimpleReportData> {

    @EJB
    private ReportsService reportsService;

    @EJB
    private StatsDbQueryProvider statDb;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CurrencyService currencyService;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private AdvertisingChannelRestrictions channelRestrictions;

    @EJB
    private AgencyAdvertiserAccountRestrictions advertiserAccountRestrictions;

    @EJB
    private AccountRestrictions accountRestrictions;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    @Restrict(restriction = "Report.run", parameters = "'conversions'")
    @Validate(validation = "Reporting.conversions", parameters = "#parameters")
    public void processExcel(ConversionsReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'conversions'")
    @Validate(validation = "Reporting.conversions", parameters = "#parameters")
    public void processCsv(ConversionsReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'conversions'")
    @Validate(validation = "Reporting.conversions", parameters = "#parameters")
    public void processHtml(ConversionsReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data));
    }

    private void run(ConversionsReportParameters parameters, AuditResultHandlerWrapper handler) {
        reportsService.execute(new Report(parameters, handler));
    }

    private class Report extends CommonAuditableReportSupport<ConversionsReportParameters> {

        public Report(ConversionsReportParameters parameters, AuditResultHandlerWrapper handler) {
            super(parameters, handler, false);
        }

        @Override
        public void prepare() {
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    Account account = em.find(Account.class, parameters.getAccountId());
                    builder.addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone())
                        .addId("account", Account.class, parameters.getAccountId())
                        .addIds("advertiser", AdvertiserAccount.class, parameters.getCampaignAdvertiserIds())
                        .addIds("campaign", Campaign.class, parameters.getCampaignIds())
                        .addIds("creativeGroup", CampaignCreativeGroup.class, parameters.getGroupIds())
                        .addIds("creative", CampaignCreative.class, parameters.getCreativeIds())
                        .addIds("advertiser", AdvertiserAccount.class, parameters.getConversionAdvertiserIds())
                        .addIds("conversions", Action.class, parameters.getConversionIds());
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            handler.registry(getRegistry(), RowTypes.data());

            prepareMetaData();

            query = statDb.queryCallable("report.conversions_report")
                .parameter("fromDate", parameters.getDateRange().getBegin(), PostgreLocalDateUserType.INSTANCE)
                .parameter("toDate", parameters.getDateRange().getEnd(), PostgreLocalDateUserType.INSTANCE)
                .parameter("p_account_id", parameters.getAccountId(), Types.INTEGER)
                .parameter("p_campaign_advertiser_ids", parameters.getCampaignAdvertiserIds(), PostgreIntArrayUserType.INSTANCE)
                .parameter("p_campaign_ids", parameters.getCampaignIds(), PostgreIntArrayUserType.INSTANCE)
                .parameter("p_ccg_ids", parameters.getGroupIds(), PostgreIntArrayUserType.INSTANCE)
                .parameter("p_cc_ids", parameters.getCreativeIds(), PostgreIntArrayUserType.INSTANCE)
                .parameter("p_action_advertiser_ids", parameters.getConversionAdvertiserIds(), PostgreIntArrayUserType.INSTANCE)
                .parameter("p_action_ids", parameters.getConversionIds(), PostgreIntArrayUserType.INSTANCE)
                .parameter("p_output_cols", ResultSetNameTransformer.getResultSetNames(metaData.getOutputColumnsMeta().getColumnsWithDependencies()), PostgreSimpleArrayUserType.INSTANCE)
                .parameter("p_metric_cols", ResultSetNameTransformer.getResultSetNames(metaData.getMetricsColumnsMeta().getColumnsWithDependencies()), PostgreSimpleArrayUserType.INSTANCE);
        }

        private void prepareMetaData() {
            metaData = ConversionsMeta.META_BY_DATE.resolve(parameters).retainById(parameters.getColumns());
            Account account = em.find(Account.class, parameters.getAccountId());
            if (currentUserService.isExternal() && !account.isPubConversionReportFlag()) {
                metaData = metaData.exclude(PUBLISHER);
            }
            Set<DbColumn> required = new HashSet<>();
            if (parameters.isShowResultsByDay()) {
                required.add(ConversionsMeta.DATE);
            }
            required.add(ConversionsMeta.POST_IMP_CONV);
            required.add(ConversionsMeta.POST_CLICK_CONV);
            metaData = metaData.include(required);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CONVERSIONS;
        }

        private ValueFormatterRegistryImpl getRegistry() {
            final boolean isExternal = currentUserService.isExternal();
            final boolean canViewAdvertiser = advertiserEntityRestrictions.canView();
            final boolean canViewPublisher = accountRestrictions.canView(AccountRole.PUBLISHER);

            String currencyCode = currencyService.getAccountCurrency(parameters.getAccountId()).getCurrencyCode();
            ValueFormatterRegistryImpl result = ValueFormatterRegistries.registry()
                .column(ADVERTISER, new ConditionEntityUrlValueFormatter(ADVERTISER_ID, isExternal ? EX_ADVERTISER_URL_PATTERN : ADVERTISER_URL_PATTERN,
                        new Condition() {
                            @Override
                            public boolean isShowUrl(FormatterContext context) {
                                return canViewAdvertiser && (!isExternal || (Boolean) context.getRow().get(ADVERTISER_VISIBLE));
                            }
                        }))
                .column(CAMPAIGN, new ConditionEntityUrlValueFormatter(CAMPAIGN_ID, CAMPAIGN_URL_PATTERN,
                    new EntityRestrictionsCondition(CAMPAIGN_VISIBLE)))
                .column(CREATIVE_GROUP, new ConditionEntityUrlValueFormatter(CREATIVE_GROUP_ID, CREATIVE_GROUP_URL_PATTERN,
                    new EntityRestrictionsCondition(CREATIVE_GROUP_VISIBLE)))
                .column(CREATIVE, new ConditionEntityUrlValueFormatter(CREATIVE_ID, CREATIVE_URL_PATTERN,
                    new EntityRestrictionsCondition(CREATIVE_VISIBLE)))
                .column(CONVERSION, new NAValueFormatter(new ConditionEntityUrlValueFormatter(CONVERSION_ID, CONVERSION_URL_PATTERN,
                        new EntityRestrictionsCondition(CONVERSION_VISIBLE))))
                .column(PUBLISHER, new ConditionEntityUrlValueFormatter(PUBLISHER_ID, PUBLISHER_URL_PATTERN, new Condition() {
                    @Override
                    public boolean isShowUrl(FormatterContext context) {
                        return canViewPublisher && (Boolean) context.getRow().get(PUBLISHER_VISIBLE);
                    }
                }))
                .column(CHANNEL, new ConditionEntityUrlValueFormatter(CHANNEL_ID, CHANNEL_URL_PATTERN,
                    new Condition() {
                        @Override
                        public boolean isShowUrl(FormatterContext context) {
                            Number accountRoleId = (Number) context.getRow().get(CHANNEL_ACCOUNT_ROLE_ID);
                            AccountRole accountRole = AccountRole.valueOf(accountRoleId.intValue());
                            return (channelRestrictions.canView(accountRole) || isExternal) &&
                                    ((Boolean) context.getRow().get(CHANNEL_VISIBLE) || !isExternal);
                        }
                    }))
                .column(CONVERSION_CATEGORY, new NAValueFormatter(new ValueFormatterSupport<Number>() {

                    @Override
                    public String formatText(Number value, FormatterContext context) {
                        if (value == null) {
                            return "";
                        }
                        return StringUtil.getLocalizedString(ConversionCategory.valueOf(value.intValue()).getNameKey());
                    }
                }))
                .column(ROI, new NAValueFormatter(new PercentValueFormatter()))
                .column(REVENUE, new CurrencyValueFormatter(currencyCode))
                .column(COST, new CurrencyValueFormatter(currencyCode));
            return result;
        }


        class EntityRestrictionsCondition implements Condition {
            private DbColumn column;
            private boolean isInternal;
            private boolean canView;

            public EntityRestrictionsCondition(DbColumn column) {
                this.column = column;
                this.isInternal = currentUserService.isInternal();
                this.canView = advertiserEntityRestrictions.canView();
            }

            @Override
            public boolean isShowUrl(FormatterContext context) {
                return canView && ((Boolean) context.getRow().get(column) || isInternal);
            }
        }

    }
}
