package app.programmatic.ui.account.service;

import app.programmatic.ui.account.dao.model.*;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.country.model.Country;
import app.programmatic.ui.country.service.CountryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static app.programmatic.ui.account.dao.model.AccountDisplayStatusParam.ALL;
import static app.programmatic.ui.account.dao.model.AccountDisplayStatusParam.INACTIVE;
import static app.programmatic.ui.account.dao.model.AccountDisplayStatusParam.LIVE;
import static app.programmatic.ui.account.dao.model.AccountDisplayStatusParam.NOT_LIVE;
import static app.programmatic.ui.account.tool.DisplayStatusHelper.getStatusMap;


@Service
public class SearchAccountServiceImpl implements SearchAccountService {
    private static final String STATUS_PROP_PREFIX = "majorStatus.";
    private static final String ACCOUNT_ROLE_PROP_PREFIX = "searchParam.accountRole.";

    @Autowired
    private DataSourceService dsService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Override
    public AdditionalSearchParams getAdditionalSearchParams() {
        List<Country> countries = countryService.findAllOrdered();
        List<CountrySearchParam> countryParams = countries.stream()
                .map( c -> new CountrySearchParam(
                        MessageInterpolator.getDefaultMessageInterpolator().interpolate("country." + c.getCountryCode()),
                        c.getCountryCode()))
                .sorted((param1, param2) -> param1.getName().compareTo(param2.getName()))
                .collect(Collectors.toList());

        AdditionalSearchParams result = new AdditionalSearchParams();
        result.setCountries(countryParams);
        result.setDisplayStatuses(getStatusSearchParams());
        result.setAccountRoles(getAccountRoleSearchParams());
        return result;
    }

    @Override
    @Restrict(restriction = "account.searchAdvertising")
    public List<AccountStats> searchAdvertisingAccounts(String name, String countryCode,
            EnumSet<AccountDisplayStatus> displayStatuses, AccountRoleParam roleParam) {
        return dsService.executeWithAuth(jdbcOperations, () -> searchWithDatastore(name, countryCode, displayStatuses, roleParam));
    }

    @Override
    @Restrict(restriction = "account.searchAdvertiserInAgency")
    public List<AdvertiserInAgencyStats> searchInAgencyAdvertisingAccounts(Long agencyId) {
        return dsService.executeWithAuth(jdbcOperations, () -> searchWithDatastore(agencyId));
    }

    private List<AccountStats> searchWithDatastore(String name, String countryCode,
        EnumSet<AccountDisplayStatus> displayStatuses, AccountRoleParam roleParam) {
        //ToDo: check is access denied to Internal Account Id

        Array array = displayStatuses == null || displayStatuses.isEmpty() ? null :
                jdbcOperations.execute((Connection con) -> con.createArrayOf("integer",
                        displayStatuses.stream()
                                .map( s -> s.getId() ).collect(Collectors.toList())
                                .toArray()));

        return jdbcOperations.query("select * from statqueries.advertiseraccountsstats(?::varchar, ?::integer, " +
                        "?::character(2), ?::integer, ?::integer[], ?::integer, ?::integer[], ?)",
                new Object[]{ name, null, countryCode, 0, null, null, array, Boolean.TRUE },
                (ResultSet rs, int index) -> convertToAccountStats(rs, roleParam))
            .stream()
            .filter( s -> s != null)
            .collect(Collectors.toList());
    }

    private List<AdvertiserInAgencyStats> searchWithDatastore(Long agencyId) {
        return jdbcOperations.query("select * from statqueries.advertiser_dashboard(?::date, ?::date, ?::int, ?, ?)",
                new Object[]{ null, null, agencyId, Boolean.TRUE, Boolean.FALSE },
                (ResultSet rs, int index) -> convertToAdvertiserInAgencyStats(rs));
    }

    private static AccountStats convertToAccountStats(ResultSet rs, AccountRoleParam roleParam) throws SQLException {
        boolean isAgency = rs.getBoolean("is_agency");
        if (    roleParam == AccountRoleParam.AGENCY && !isAgency ||
                roleParam == AccountRoleParam.ADVERTISER && isAgency) {
            return null;
        }

        AccountStats result = new AccountStats();
        result.setId(rs.getLong("account_id"));
        result.setName(rs.getString("name"));
        result.setCurrencyCode(rs.getString("currency_code"));
        result.setImpressions(rs.getLong("imps"));
        result.setClicks(rs.getLong("clicks"));
        result.setCtr(rs.getBigDecimal("ctr").movePointRight(2)); // This DB proc doesn't multiply a value by 100
        result.setRevenue(rs.getBigDecimal("adv_amount"));
        result.setDisplayStatus(getStatusMap().get(rs.getInt("display_status_id")));
        result.setTestFlag(rs.getBoolean("is_test"));
        result.setAgency(isAgency);

        return result;
    }

    private static AdvertiserInAgencyStats convertToAdvertiserInAgencyStats(ResultSet rs) throws SQLException {
        AdvertiserInAgencyStats result = new AdvertiserInAgencyStats();

        result.setImps(rs.getLong("imps"));
        result.setClicks(rs.getLong("clicks"));
        result.setCtr(rs.getBigDecimal("ctr"));
        result.setCampaignCreditUsed(rs.getBigDecimal("campaign_credit_used"));
        result.setTargetingCost(rs.getBigDecimal("targeting_cost"));
        result.setInventoryCost(rs.getBigDecimal("inventory_cost"));
        result.setTotalValue(rs.getBigDecimal("total_value"));
        result.setTotalCost(rs.getBigDecimal("total_cost"));
        result.setEcpm(rs.getBigDecimal("ecpm"));
        result.setSelfServiceCost(rs.getBigDecimal("cost"));
        result.setCcgsPendingUser(rs.getLong("pending_ccgs_user"));
        result.setCreativesPendingUser(rs.getLong("pending_creatives_user"));
        result.setCreativesPendingForos(rs.getLong("pending_creatives_foros"));
        result.setAdvertiserId(rs.getLong("account_id"));
        result.setAdvertiserName(rs.getString("name"));
        result.setDisplayStatus(getStatusMap().get(rs.getInt("display_status_id")));

        return result;
    }

    private static List<StatusSearchParam> getStatusSearchParams() {
        return Arrays.asList(
                getStatusSearchParams(ALL),
                getStatusSearchParams(LIVE),
                getStatusSearchParams(NOT_LIVE),
                getStatusSearchParams(INACTIVE)
        );
    }

    private static StatusSearchParam getStatusSearchParams(AccountDisplayStatusParam statusParam) {
        StatusSearchParam result = new StatusSearchParam();
        result.setType(statusParam);
        result.setName(MessageInterpolator.getDefaultMessageInterpolator().interpolate(STATUS_PROP_PREFIX + statusParam.toString()));

        return result;
    }

    private static List<AccountRoleSearchParam> getAccountRoleSearchParams() {
        return Arrays.asList(
                getAccountRoleSearchParams(AccountRoleParam.ALL),
                getAccountRoleSearchParams(AccountRoleParam.AGENCY),
                getAccountRoleSearchParams(AccountRoleParam.ADVERTISER)
        );
    }

    private static AccountRoleSearchParam getAccountRoleSearchParams(AccountRoleParam role) {
        String roleName = MessageInterpolator.getDefaultMessageInterpolator().interpolate(
                ACCOUNT_ROLE_PROP_PREFIX + (role == null ? "ALL" : role.toString()));
        return new AccountRoleSearchParam(role, roleName);
    }
}
