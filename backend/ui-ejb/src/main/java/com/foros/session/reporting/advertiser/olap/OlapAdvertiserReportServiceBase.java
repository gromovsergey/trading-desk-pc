package com.foros.session.reporting.advertiser.olap;

import com.foros.model.Context;
import com.foros.model.ExtensionProperty;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CampaignType;
import com.phorm.oix.olap.OlapIdentifier;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.AggregatableColumn;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.formatter.*;
import com.foros.reporting.serializer.formatter.registry.DefaultFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.reporting.tools.olap.query.OlapQuery;
import com.foros.reporting.tools.olap.query.OlapQueryProvider;
import com.foros.reporting.tools.subtotal.SubTotalHandlerWrapper;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.account.AccountService;
import com.foros.session.campaignCredit.CampaignCreditService;
import com.foros.session.reporting.CommonAuditableOlapReportSupport;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportsService;
import com.foros.session.reporting.SummarySerializerWrapper;
import com.foros.session.security.UserService;
import org.joda.time.LocalDate;
import org.olap4j.mdx.IdentifierNode;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

public abstract class OlapAdvertiserReportServiceBase implements OlapAdvertiserReportService {
    public final static ExtensionProperty<Boolean> IS_GROSS = new ExtensionProperty<>(Boolean.class);
    public final static ExtensionProperty<Boolean> IS_DISPLAY = new ExtensionProperty<>(Boolean.class);

    public final static ExtensionProperty<OlapAdvertiserReportParameters> PARAMETERS = new ExtensionProperty<>(OlapAdvertiserReportParameters.class);

    @EJB
    protected OlapQueryProvider queryProvider;

    @EJB
    protected ReportsService reportsService;

    @EJB
    protected CurrentUserService currentUserService;

    @EJB
    protected UserService userService;

    @EJB
    protected AccountService accountService;

    @EJB
    protected CampaignCreditService campaignCreditService;

    @EJB
    protected LoggingJdbcTemplate jdbcTemplate;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    protected Set<OlapColumn> toDbColumns(MetaData<OlapColumn> available, Set<String> columns) {
        HashSet<OlapColumn> res = new HashSet<>();
        if (columns == null) {
            return res;
        }

        for (String column : columns) {
            if (available.contains(column)) {
                OlapColumn dbColumn = available.find(column);
                res.add(dbColumn);
            }
            // not available
        }
        return res;
    }

    protected Set<OlapColumn> processColumns(Collection<OlapColumn> columns, OlapAdvertiserReportParameters params) {
        for (OlapAdvertiserMeta.NetGrossPair netGrossPair: OlapAdvertiserMeta.NET_GROSS_TRIPLETS.values()) {
            columns.remove(netGrossPair.getNet());
            columns.remove(netGrossPair.getGross());
        }

        for (List<OlapColumn> wgPair : OlapAdvertiserMeta.WG_TRIPLETS.values()) {
            columns.removeAll(wgPair);
        }

        Set<OlapColumn> res = new HashSet<>(columns.size());
        // add date columns
        if (params.getUnitOfTime() != null) {
            res.add(params.getUnitOfTime().getColumn());
        }

        OlapColumnsSplitter splitter = newColumnsSplitter(new OlapColumnsSplitter.Result(res), params);
        for (OlapColumn dbColumn : columns) {
            splitter.process(dbColumn);
        }

        return res;
    }

    protected OlapColumnsSplitter newColumnsSplitter(OlapColumnsSplitter splitter, OlapAdvertiserReportParameters params) {
        return new OlapColumnsSplitter.NetGross(splitter, params.getCostAndRates());
    }

    @Override
    public OlapAdvertiserReportState getReportState(OlapAdvertiserReportParameters params, boolean ignoreSelected) {
        OlapAdvertiserReportDescription description = getDescription(params);

        Set<OlapColumn> toExclude = calculateAvailableMetaData(new HashSet<OlapColumn>(), params, description);

        ReportMetaData<OlapColumn> available = description.getResolvableMetaData().resolve(params).exclude(toExclude);
        Set<OlapColumn> newDefault = new HashSet<>(description.getDefaultColumns());
        newDefault.retainAll(available.getColumns());

        Set<OlapColumn> selectedColumns;
        if (ignoreSelected) {
            selectedColumns = newDefault;
        } else {
            selectedColumns = toDbColumns(available, params.getColumns());
        }
        selectedColumns = processColumns(selectedColumns, params);
        selectedColumns.addAll(description.getFixedColumns());
        ReportMetaData<OlapColumn> selected = available.retain(selectedColumns);

        return new OlapAdvertiserReportState(
                available,
                selected,
                description.getFixedColumns(),
                description.getSubtotalLevels()
        );
    }

    protected abstract OlapAdvertiserReportDescription getDescription(OlapAdvertiserReportParameters params);

    protected abstract PreparedParameterBuilder.Factory getParameterBuilderFactory(OlapAdvertiserReportParameters params);

    protected Set<OlapColumn> calculateAvailableMetaData(Set<OlapColumn> toExclude, OlapAdvertiserReportParameters params,
            OlapAdvertiserReportDescription description) {
        // cost values only for internal
        if (currentUserService.isExternal()) {
            toExclude.add(OlapAdvertiserMeta.MARGIN);
            toExclude.add(OlapAdvertiserMeta.ECPM);
            toExclude.add(OlapAdvertiserMeta.ECPM_KW);
            toExclude.add(OlapAdvertiserMeta.COST);
            toExclude.add(OlapAdvertiserMeta.AVERAGE_ACTUAL_CPC);
            toExclude.add(OlapAdvertiserMeta.ECPU);

            for ( OlapAdvertiserMeta.NetGrossPair pair : OlapAdvertiserMeta.NET_GROSS_TRIPLETS.values()) {
                toExclude.add(pair.getGross());
                toExclude.add(pair.getNet());
            }
            toExclude.addAll(OlapAdvertiserMeta.HID_COLUMNS);
        }

        AdvertisingAccountBase advertisingAccount = params.getAccountId() == null ? null :
                (AdvertisingAccountBase) accountService.find(params.getAccountId());
        if (advertisingAccount == null || !advertisingAccount.isSelfServiceFlag()) {
            // Not self service
            toExclude.add(OlapAdvertiserMeta.SELF_SERVICE_COST);
            toExclude.add(OlapAdvertiserMeta.SELF_SERVICE_COST_NET);
            toExclude.add(OlapAdvertiserMeta.SELF_SERVICE_COST_GROSS);
        } else {
            boolean isVatEnabled = advertisingAccount.getCountry().isVatEnabled();
            if (isVatEnabled) {
                // So we had to show NET and GROSS
                toExclude.add(OlapAdvertiserMeta.SELF_SERVICE_COST);
            } else {
                // So we will show w/o NET and GROSS to prevent confusing
                toExclude.add(OlapAdvertiserMeta.SELF_SERVICE_COST_NET);
                toExclude.add(OlapAdvertiserMeta.SELF_SERVICE_COST_GROSS);
            }
        }

        // All unique users columns are unavailable by default
        toExclude.addAll(OlapAdvertiserMeta.UNIQUE_USERS_COLUMNS);

        if (isUniqueUsersAllowed(params)) {
            includeAllowedUniqueUsers(toExclude, params);
        }

        // We need Daily/Monthly Unique users to calculate eCPU
        if (toExclude.containsAll(Arrays.asList(OlapAdvertiserMeta.DAILY_UNIQUE_USERS, OlapAdvertiserMeta.MONTHLY_UNIQUE_USERS))) {
            toExclude.add(OlapAdvertiserMeta.ECPU);
            toExclude.add(OlapAdvertiserMeta.ECPU_NET);
            toExclude.add(OlapAdvertiserMeta.ECPU_GROSS);
        }

        if (!isCreditedColumnsAvailable(params)) {
            toExclude.addAll(OlapAdvertiserMeta.CREDITED_COLUMNS);
        }

        if (!isHIDColumnsAvailable(params)) {
            toExclude.addAll(OlapAdvertiserMeta.HID_COLUMNS);
        }

        return toExclude;
    }

    private boolean isCreditedColumnsAvailable(OlapAdvertiserReportParameters params) {
        Long accountId = params.getAccountId();
        return (accountId != null && campaignCreditService.hasCampaignCredits(accountId))
                && OlapDetailLevel.Keyword != params.getReportType();
    }

    protected boolean isHIDColumnsAvailable(OlapAdvertiserReportParameters params) {
        return params.getReportType() == OlapDetailLevel.Campaign;
    }

    protected boolean isUniqueUsersAllowed(OlapAdvertiserReportParameters params) {
        if (params.getReportType() == OlapDetailLevel.Account) {
            // not selected yet
            if (params.getAccountId() != null) {
                AdvertisingAccountBase account = findAccount(params.getAccountId());
                return account instanceof AdvertiserAccount;
            }
        }

        return true;
    }

    private void includeAllowedUniqueUsers(Set<OlapColumn> toExclude,  OlapAdvertiserReportParameters params) {
        if (params.getUnitOfTime() == null) {
            // summary
            toExclude.remove(OlapAdvertiserMeta.TOTAL_UNIQUE_USERS);
            toExclude.remove(OlapAdvertiserMeta.NEW_UNIQUE_USERS);

            LocalDate begin = params.getDateRange().getBegin();
            LocalDate end = params.getDateRange().getEnd();
            if (begin.equals(end)) {
                // 1 day is selected
                toExclude.remove(OlapAdvertiserMeta.DAILY_UNIQUE_USERS);
            }
        } else {
            switch (params.getUnitOfTime()) {
                case DATE:
                    toExclude.remove(OlapAdvertiserMeta.TOTAL_UNIQUE_USERS);
                    toExclude.remove(OlapAdvertiserMeta.NEW_UNIQUE_USERS);
                    toExclude.remove(OlapAdvertiserMeta.DAILY_UNIQUE_USERS);
                    toExclude.remove(OlapAdvertiserMeta.MONTHLY_UNIQUE_USERS);
                    break;
                case WEEK_MON_SUN:
                case WEEK_SUN_SAT:
                case MONTH:
                case QUARTER:
                case YEAR:
                    toExclude.remove(OlapAdvertiserMeta.TOTAL_UNIQUE_USERS);
                    toExclude.remove(OlapAdvertiserMeta.NEW_UNIQUE_USERS);
                    break;
            }
        }
    }

    protected AdvertisingAccountBase findAccount(Long id) {
        return em.find(AdvertisingAccountBase.class, id);
    }

    @Override
    public Set<OlapColumn> getRecommendedColumns(OlapAdvertiserReportParameters params) {
        if (params.getAccountId() == null || params.getDateRange() == null) {
            return Collections.emptySet();
        }

        if (!isCreditedColumnsAvailable(params)) {
            return Collections.emptySet();
        }

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "select * from statqueries.adv_credited_stats_existence(?::date, ?::date, ?::int, ?::varchar)",
                params.getDateRange().getBegin(),
                params.getDateRange().getEnd(),
                params.getAccountId(),
                getCampaignType() == null ? null : getCampaignType().getLetter()
        );
        rs.next();
        boolean creditedImpsExist = rs.getBoolean("credited_imps_exist");
        boolean creditedClicksExist = rs.getBoolean("credited_clicks_exist");
        boolean creditedActionsExist = rs.getBoolean("credited_actions_exist");

        Set<OlapColumn> columns = new HashSet<>(8);

        if (creditedImpsExist) {
            if (params.isSplitWalledGardenStatistics()) {
                columns.addAll(OlapAdvertiserMeta.WG_TRIPLETS.get(OlapAdvertiserMeta.CREDITED_IMPRESSIONS));
                columns.addAll(OlapAdvertiserMeta.WG_TRIPLETS.get(OlapAdvertiserMeta.CAMPAIGN_CREDIT_USED));
            } else {
                columns.add(OlapAdvertiserMeta.CREDITED_IMPRESSIONS);
                columns.add(OlapAdvertiserMeta.CAMPAIGN_CREDIT_USED);
            }
        }

        if (creditedClicksExist) {
            if (params.isSplitWalledGardenStatistics()) {
                columns.addAll(OlapAdvertiserMeta.WG_TRIPLETS.get(OlapAdvertiserMeta.CREDITED_CLICKS));
                columns.addAll(OlapAdvertiserMeta.WG_TRIPLETS.get(OlapAdvertiserMeta.CAMPAIGN_CREDIT_USED));
            } else {
                columns.add(OlapAdvertiserMeta.CREDITED_CLICKS);
                columns.add(OlapAdvertiserMeta.CAMPAIGN_CREDIT_USED);
            }
        }

        if (creditedActionsExist) {
            if (params.isSplitWalledGardenStatistics()) {
                columns.addAll(OlapAdvertiserMeta.WG_TRIPLETS.get(OlapAdvertiserMeta.CREDITED_ACTIONS));
                columns.addAll(OlapAdvertiserMeta.WG_TRIPLETS.get(OlapAdvertiserMeta.CAMPAIGN_CREDIT_USED));
            } else {
                columns.add(OlapAdvertiserMeta.CREDITED_ACTIONS);
                columns.add(OlapAdvertiserMeta.CAMPAIGN_CREDIT_USED);
            }
        }

        return columns;
    }

    protected abstract CampaignType getCampaignType();

    protected abstract class Report extends CommonAuditableOlapReportSupport<OlapAdvertiserReportParameters> {
        protected boolean executeSummary;
        protected AdvertisingAccountBase account;
        protected Context context;
        protected ValueFormatterRegistry registry;
        protected OlapAdvertiserReportState reportState;

        public Report(OlapAdvertiserReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler);
            this.executeSummary = executeSummary;
        }

        protected abstract String getOlapQueryType();

        @Override
        public void prepare() {
            // some preparations
            context = new Context();
            account = findAccount(parameters.getAccountId());
            context.setProperty(IS_GROSS, account.getAccount().getAccountType().isInputRatesAndAmountsGross());
            context.setProperty(IS_DISPLAY, CampaignType.TEXT != getCampaignType());
            context.setProperty(PARAMETERS, parameters);

            preparedParameterBuilderFactory = newParametersFactory(parameters);
            handler.preparedParameters(preparedParameterBuilderFactory);
            reportState = getReportState(parameters, false);

            metaData = getDescription(parameters)
                    .getResolvableMetaData()
                    .resolve(parameters)
                    .retainById(reportState.getSelectedColumnNames(), reportState.getSelectedColumnNames());

            initReg();
        }

        protected abstract PreparedParameterBuilder.Factory newParametersFactory(OlapAdvertiserReportParameters parameters);

        protected void initReg() {
            // handler
            preparedParameterBuilderFactory = getParameterBuilderFactory(parameters);

            CurrencyValueFormatter currencyFormatter = new CurrencyValueFormatter(account.getCurrency().getCurrencyCode());
            NAValueFormatter naCurrencyFormatter = new NAValueFormatter(currencyFormatter, Styles.number());

            RateValueFormatter rateFormatter = new RateValueFormatter(currencyFormatter);

            ValueFormatterRegistryImpl r = ValueFormatterRegistries.registry()
                    .type(ColumnTypes.currency(), currencyFormatter)
                    // dates
                    .column(OlapAdvertiserMeta.WEEK_MON_SUN, new WeekValueFormatter())
                    .column(OlapAdvertiserMeta.WEEK_SUN_SAT, new WeekValueFormatter())
                    .column(OlapAdvertiserMeta.MONTH, new MonthValueFormatter())
                    .column(OlapAdvertiserMeta.QUARTER, new QuarterValueFormatter())
                    // rate for inventory
                    .column(OlapAdvertiserMeta.RATE_FOR_INVENTORY, rateFormatter)
                    .column(OlapAdvertiserMeta.RATE_FOR_INVENTORY_NET, rateFormatter)
                    .column(OlapAdvertiserMeta.RATE_FOR_INVENTORY_GROSS, rateFormatter)
                    // average actual cpc
                    .column(OlapAdvertiserMeta.AVERAGE_ACTUAL_CPC, naCurrencyFormatter)
                    .column(OlapAdvertiserMeta.AVERAGE_ACTUAL_CPC_NET, naCurrencyFormatter)
                    .column(OlapAdvertiserMeta.AVERAGE_ACTUAL_CPC_GROSS, naCurrencyFormatter)
                    // current cpc bid
                    .column(OlapAdvertiserMeta.CURRENT_CPC_BID, naCurrencyFormatter)
                    .column(OlapAdvertiserMeta.CURRENT_CPC_BID_NET, naCurrencyFormatter)
                    .column(OlapAdvertiserMeta.CURRENT_CPC_BID_GROSS, naCurrencyFormatter)
                    // monthly unique users
                    .column(OlapAdvertiserMeta.MONTHLY_UNIQUE_USERS, new OlapMonthlyUniqueUsersValueFormatter(
                            new NumberValueFormatter(),
                            parameters.getUnitOfTime(),
                            parameters.getDateRange().getBegin(),
                            OlapAdvertiserMeta.MONTH
                    ));

            registry = ValueFormatterRegistries.chain()
                    .registry(DefaultFormatterRegistry.DEFAULT_REGISTRY)
                    .registry(r);

            handler.registry(registry, RowTypes.data());
            handler.registry(new OlapAdvertiserReportHeaderFormatterRegistry(metaData), RowTypes.header());
            handler.registry(registry, RowTypes.summary());
            handler.preparedParameters(preparedParameterBuilderFactory);
        }

        @Override
        protected ResultHandler wrap(ResultSerializer handler) {
            if (executeSummary) {
                handler = new SummarySerializerWrapper(handler, metaData.retain(OlapAdvertiserMeta.SUMMARY_COLUMNS));
            }

            if (!parameters.isAddSubtotals()) {
                return handler;
            }

            if (metaData.getOutputColumns().size() <= 1) {
                return handler;
            }

            OlapAdvertiserReportParameters.UnitOfTime unitOfTime = parameters.getUnitOfTime();
            List<Set<OlapColumn>> subtotalLevels = reportState.getSubtotalLevels();

            if (subtotalLevels.isEmpty() && unitOfTime == null) {
                return handler;
            }

            Set<OlapColumn> outputColumns = new HashSet<>(metaData.getOutputColumns());
            List<OlapColumn> subtotalColumns = new ArrayList<>(subtotalLevels.size());

            for(Set<OlapColumn> level: subtotalLevels) {
                for (OlapColumn column: level) {
                    if(outputColumns.contains(column)) {
                        subtotalColumns.add(column);
                        break;
                    }
                }
            }

            if (subtotalColumns.size() <= 1) {
                return handler;
            }

            // last level should be removed
            subtotalColumns.remove(subtotalColumns.size() - 1);

            Set<AggregatableColumn> aggregateColumns = new HashSet<AggregatableColumn>(metaData.getMetricsColumnsMeta().getColumnsWithDependencies());
            ResultHandler res = new SubTotalHandlerWrapper(handler, subtotalColumns, aggregateColumns);

            List<OlapColumn> columns = metaData.getColumns();

            for (OlapColumn subtotalColumn : subtotalColumns) {
                int subtotalColumnIndex = columns.indexOf(subtotalColumn);
                OlapColumn subtotalTextColumn = columns.get(subtotalColumnIndex + 1);
                List<OlapColumn> highlightColumns = columns.subList(subtotalColumnIndex + 1, columns.size());
                RowType rowType = RowTypes.subTotal(subtotalColumn);
                handler.registry(new OlapSubtotalRegistry(registry, subtotalColumn, subtotalTextColumn, highlightColumns), rowType);
            }

            return res;
        }

        @Override
        protected OlapQuery buildQuery() {
            OlapDetailLevel reportType = parameters.getReportType();

            OlapQuery res = queryProvider.query(reportType.getCube(), context)
                    .limit(handler.getMaxRows() + 1)
                    .columns(metaData.getMetricsColumnsMeta().getColumnsWithDependencies())
                    .columns(metaData.getOutputColumnsMeta().getColumnsWithDependencies())
                    .filter(OlapAdvertiserMeta.Levels.DATE_VALUE, parameters.getDateRange());

            if (parameters.getUnitOfTime() != null) {
                OlapIdentifier identifier = parameters.getUnitOfTime().getColumn().getMember(context);
                String hierarchy = "[" + identifier.getSegments().get(0) + "]";
                OlapIdentifier dateLevel = (new OlapIdentifier(IdentifierNode.parseIdentifier(hierarchy))).append(OlapAdvertiserMeta.Levels.DATE.getUniqueName());
                res.rows(dateLevel, parameters.getDateRange());
            }

            OlapIdentifier typeIdentifier = reportType.resolveIdentifier(OlapDetailLevel.Filter.Type);
            if (typeIdentifier != null) {
                res.filter(typeIdentifier, getOlapQueryType());
            }

            for (OlapDetailLevel.Filter filter : reportType.getAvailableFilters()) {
                OlapIdentifier identifier = reportType.resolveIdentifier(filter);
                switch (filter) {
                    case Advertiser:
                        List<Long> permittedAdvertiserIds = filterAdvertiserIds(
                                account.getRole() == AccountRole.AGENCY ? parameters.getAdvertiserIds() : Collections.<Long>emptyList());
                        res.row(identifier, permittedAdvertiserIds, false);
                        break;
                    case Campaign:
                        res.row(identifier, parameters.getCampaignIds(), false);
                        break;
                    case Group:
                        res.row(identifier, parameters.getCcgIds(), false);
                        break;
                    case Creative:
                        res.row(identifier, parameters.getCampaignCreativeIds(), false);
                        break;
                    case Keyword:
                        res.row(identifier, parameters.getKeyword());
                        break;
                }
            }

            if (account.getRole() == AccountRole.AGENCY) {
                OlapIdentifier agencyIdentifier = reportType.resolveIdentifier(OlapDetailLevel.Filter.Agency);
                res.row(agencyIdentifier, parameters.getAccountId());
            } else {
                OlapIdentifier advertiserIdentifier = reportType.resolveIdentifier(OlapDetailLevel.Filter.Advertiser);
                res.row(advertiserIdentifier, parameters.getAccountId());
            }

            res.order(metaData.getSortColumns());
            return res;
        }

        private List<Long> filterAdvertiserIds(List<Long> selectedIds) {
            if (currentUserService.getUser().isAdvLevelAccessFlag()) {
                List<AdvertiserAccount> advertisers = userService.findUserAdvertisers(currentUserService.getUser().getId());
                List<Long> result = new ArrayList<>(advertisers.size());

                for (AdvertiserAccount advertiser : advertisers) {
                    if (isIdSelected(advertiser.getId(), selectedIds)) {
                        result.add(advertiser.getId());
                    }
                }

                return result;
            }

            return selectedIds;
        }

        private boolean isIdSelected(Long id, List<Long> selectedIds) {
            if (selectedIds.isEmpty()) {
                return true;
            }

            for (Long selectedId : selectedIds) {
                if (selectedId.equals(id)) {
                    return true;
                }
            }

            return false;
        }
    }
}
