package app.programmatic.ui.account.service;

import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import app.programmatic.ui.account.dao.model.*;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.channel.restriction.ChannelRestrictions;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.datasource.tools.RsTools;
import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.common.foros.service.ForosAccountService;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.common.tool.foros.StatusHelper;
import app.programmatic.ui.file.service.FileService;
import app.programmatic.ui.common.restriction.annotation.Restrict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserRole;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static app.programmatic.ui.account.tool.DisplayStatusHelper.getStatusMap;
import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;


@Service
public class AccountServiceImpl implements AccountService {
    public static final long TEST_FLAG = 0x01;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private ForosAccountService forosService;

    @Autowired
    private FileService fileService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ChannelRestrictions channelRestrictions;

    @Override
    @Restrict(restriction = "account.viewAdvertising")
    public AdvertisingAccount findAdvertising(Long id) {
        return findAdvertisingUnchecked(id);
    }

    @Override
    public AdvertisingAccount findAdvertisingUnchecked(Long id) {
        return jdbcOperations.queryForObject("select a.name, a.role_id, a.agency_account_id, " +
                        "    a.display_status_id, c.currency_code, c.fraction_digits, a.country_code, " +
                        "    fd.prepaid_amount, fs.commission, tz.tzname, (act.flags::int & x'10'::int)::bool as is_gross, " +
                        "    (act.flags::int & x'2'::int)::bool as is_financial_fields, a.self_service_commission, " +
                        "    ((coalesce(agn.flags, a.flags))::int & x'100'::int)::bool as is_self_service, cn.vat_enabled " +
                        "from account a " +
                        "  left join account agn on agn.account_id = a.agency_account_id  " +
                        "  inner join accounttype act on act.account_type_id = coalesce(a.account_type_id, agn.account_type_id) " +
                        "  inner join timezone tz on tz.timezone_id = a.timezone_id " +
                        "  inner join currency c on c.currency_id = a.currency_id " +
                        "  inner join country cn on cn.country_code = a.country_code " +
                        "  left join accountfinancialdata fd on fd.account_id = a.account_id " +
                        "  left join accountfinancialsettings fs on fs.account_id = a.account_id " +
                        "where a.account_id = ? and (a.role_id = ? or a.role_id = ?)",
                new Object[]{
                        id,
                        AccountRole.ADVERTISER.getId(),
                        AccountRole.AGENCY.getId()
                },
                (ResultSet rs, int index) -> convertToAdvertisingAccount(rs, id));
    }

    private static AdvertisingAccount convertToAdvertisingAccount(ResultSet rs, Long id) throws SQLException {
        AdvertisingAccount result = new AdvertisingAccount();

        result.setId(id);
        result.setAgencyId(RsTools.getNullableLong(rs, "agency_account_id"));
        result.setName(rs.getString("name"));
        result.setRole(AccountRole.valueOf(rs.getInt("role_id")));
        result.setDisplayStatus(getStatusMap().get(rs.getInt("display_status_id")));
        result.setCurrencyCode(rs.getString("currency_code"));
        result.setCurrencyAccuracy(rs.getInt("fraction_digits"));
        result.setCountryCode(rs.getString("country_code"));
        result.setTimeZone(rs.getString("tzname"));
        result.setPrepaidAmount(rs.getBigDecimal("prepaid_amount"));
        BigDecimal commission = rs.getBigDecimal("commission");
        result.setCommission(commission != null ? commission.movePointRight(2) : null);
        result.setGrossFlag(rs.getBoolean("is_gross"));
        result.setFinancialFieldsFlag(rs.getBoolean("is_financial_fields"));
        result.setSelfServiceFlag(rs.getBoolean("is_self_service"));
        BigDecimal selfServiceCommission = rs.getBigDecimal("self_service_commission");
        result.setSelfServiceCommission(selfServiceCommission != null ? selfServiceCommission.movePointRight(2) : null);
        result.setVatEnabledFlag(rs.getBoolean("vat_enabled"));

        return result;
    }

    @Override
    public PublisherAccount findPublisherUnchecked(Long id) {
        return jdbcOperations.queryForObject("select a.name, a.display_status_id, c.currency_code, c.fraction_digits " +
                        "from account a " +
                        "  inner join currency c on c.currency_id = a.currency_id " +
                        "where a.account_id = ? and a.role_id = ?",
                new Object[]{id, AccountRole.PUBLISHER.getId()},
                (ResultSet rs, int index) -> {
                    PublisherAccount publisher = new PublisherAccount();
                    publisher.setId(id);
                    publisher.setName(rs.getString("name"));
                    publisher.setDisplayStatus(getStatusMap().get(rs.getInt("display_status_id")));
                    publisher.setCurrencyCode(rs.getString("currency_code"));
                    publisher.setCurrencyAccuracy(rs.getInt("fraction_digits"));
                    return publisher;
                });
    }

    @Override
    @Restrict(restriction = "account.findPublishers")
    public List<IdName> findPublishers() {
        return jdbcOperations.query("select a.account_id, a.name " +
                        "from account a " +
                        "where a.role_id = ? and a.country_code = ? and a.status != 'D' " +
                        "order by a.name",
                new Object[]{
                        AccountRole.PUBLISHER.getId(),
                        LOCALE_RU.getCountry()
                },
                (ResultSet rs, int index) ->
                    new IdName(rs.getLong("account_id"), rs.getString("name"))
                );
    }


    @Override
    @Restrict(restriction = "account.findPublishers")
    public List<IdName> findPublishersForReferrerReport() {
        return jdbcOperations.query("select a.account_id, a.name " +
                        "from account a " +
                        "inner join site s using(account_id) " +
                        "inner join tags t using(site_id) " +
                        "where a.role_id = ? and a.country_code = ? " +
                        "and a.status != 'D' " +
                        "and s.status != 'D' " +
                        "and t.status != 'D' " +
                        "and (a.flags::int & ?::int)::bool = false " +
                        "group by a.account_id " +
                        "order by a.name",
                new Object[]{
                        AccountRole.PUBLISHER.getId(),
                        LOCALE_RU.getCountry(),
                        TEST_FLAG
                },
                (ResultSet rs, int index) ->
                        new IdName(rs.getLong("account_id"), rs.getString("name"))
        );
    }

    @Override
    public List<IdName> findAllChannelOwners() {
        User currentUser = authorizationService.getAuthUser();
        UserRole currentUserRole = currentUser.getUserRole();

        StringBuilder query = new StringBuilder("select a.account_id, a.name from account a ");
        query.append("where a.agency_account_id is null ");
        query.append("and (a.role_id = ").append(AccountRole.INTERNAL.getId()).append(" ");

        boolean listAdvertisers = channelRestrictions.canView(AccountRole.ADVERTISER);
        boolean listAgencies = channelRestrictions.canView(AccountRole.AGENCY);
        if (listAdvertisers || listAgencies) {
            query.append("or (a.role_id in (")
                    .append(listAdvertisers ? AccountRole.ADVERTISER.getId() + (listAgencies ? ", " : "") : "")
                    .append(listAgencies ? AccountRole.AGENCY.getId() : "")
                    .append(") ");

            if (currentUserRole.getInternalAccessType() == null) {
                return Collections.emptyList();
            }

            switch (currentUserRole.getInternalAccessType()) {
                case M:
                    Set<Long> accessAccountIds = currentUserRole.getAccessAccountIds();
                    accessAccountIds.add(currentUser.getAccountId());
                    query.append("and a.internal_account_id in (")
                            .append(accessAccountIds.stream()
                                    .map(value -> value.toString())
                                    .collect(Collectors.joining(", ")))
                            .append(") ");
                case U:
                    query.append("and a.internal_account_id = ")
                            .append(currentUser.getAccountId())
                            .append(" ");
            }

            if (currentUserRole.isAccountManager()) {
                query.append("and a.account_manager_id = ")
                .append(currentUser.getId())
                .append(" ");
            }

            query.append(") ");
        }

        query.append(") ");
        query.append("and a.status != 'D' ");
        query.append("order by a.name");

        return jdbcOperations.query(query.toString(),
                new Object[0],
                (ResultSet rs, int index) ->
                        new IdName(rs.getLong("account_id"), rs.getString("name"))
        );
    }

    @Override
    public List<IdName> findInternalAccounts() {
        User currentUser = authorizationService.getAuthUser();
        UserRole currentUserRole = currentUser.getUserRole();

        StringBuilder query = new StringBuilder("select a.account_id, a.name from account a ");
        query.append("where a.role_id = ").append(AccountRole.INTERNAL.getId()).append(" ");
        query.append("and a.status != 'D' ");

        switch (currentUserRole.getInternalAccessType()) {
            case M:
                Set<Long> accessAccountIds = currentUserRole.getAccessAccountIds();
                accessAccountIds.add(currentUser.getAccountId());
                query.append("and a.account_id in (")
                        .append(accessAccountIds.stream()
                                .map(value -> value.toString())
                                .collect(Collectors.joining(", ")))
                        .append(") ");
            case U:
                query.append("and a.account_id = ")
                        .append(currentUser.getAccountId())
                        .append(" ");
        }

        query.append("order by a.name");

        return jdbcOperations.query(query.toString(),
                new Object[0],
                (ResultSet rs, int index) ->
                        new IdName(rs.getLong("account_id"), rs.getString("name"))
        );
    }

    @Override
    public Account findAccountUnchecked(Long id) {
        return jdbcOperations.queryForObject("select " +
                        "a.name, a.role_id, a.country_code, a.internal_account_id, a.account_manager_id " +
                        "from account a " +
                        "where a.account_id = ?",
                new Object[]{id},
                (ResultSet rs, int index) -> {
                    Account account = new Account();
                    account.setId(id);
                    account.setRole(AccountRole.valueOf(rs.getInt("role_id")));
                    account.setCountryCode(rs.getString("country_code"));
                    account.setInternalAccountId(RsTools.getNullableLong(rs, "internal_account_id"));
                    account.setAccountManagerId(RsTools.getNullableLong(rs, "account_manager_id"));
                    return account;
                });
    }

    @Override
    @Restrict(restriction = "account.viewAdvertising")
    public BigDecimal getAccountAvailableBudget(Long id) {
        // Returns null if financial settings on another account level
        // ToDo: Get rid of accordant flag checking after Issue #474
        return jdbcOperations.queryForObject("select " +
                        "case when ((a.agency_account_id is not null) = (coalesce(at.flags, agat.flags) & 2)::bool) " +
                        "    then account.get_credit_balance(a.account_id) " +
                        "    else null::numeric " +
                        "end " +
                        "from account a " +
                        "left join accounttype at using(account_type_id) " +
                        "left join account ag on ag.account_id = a.agency_account_id " +
                        "left join accounttype agat on agat.account_type_id = ag.account_type_id " +
                        "where a.account_id = ?::int",
                new Object[] { id },
                (ResultSet rs, int index) -> rs.getBigDecimal(1)
        );
    }

    @Override
    @Restrict(restriction = "audienceResearch.edit")
    public List<AccountEntity> findAdvertisingList() {
        return jdbcOperations.query("select a.account_id, a.name, a.display_status_id " +
                        "from account a " +
                        "where (a.role_id = ? or (a.role_id = ? and a.agency_account_id is null)) " +
                        "and a.country_code = ? " +
                        "and a.status != 'D' " +
                        "order by a.name",
                new Object[]{
                        AccountRole.AGENCY.getId(),
                        AccountRole.ADVERTISER.getId(),
                        LOCALE_RU.getCountry()
                },
                (ResultSet rs, int index) -> {
                    AccountEntity account = new AccountEntity();
                    account.setId(rs.getLong("account_id"));
                    account.setName(rs.getString("name"));
                    account.setDisplayStatus(AccountDisplayStatus.valueOf(rs.getInt("display_status_id")));
                    return account;
                }
        );
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.account.validation.ForosAccountViolationsServiceImpl")
    @Restrict(restriction = "account.createAdvertiserInAgency", parameters = "advertiserInAgency.agencyId")
    public Long createAdvertiserInAgency(AdvertisingAccount advertiserInAgency) {
        advertiserInAgency.setDisplayStatus(AccountDisplayStatus.LIVE.name());
        return processAdvertiserAsAdmin(advertiserInAgency, OperationType.CREATE);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.account.validation.ForosAccountViolationsServiceImpl")
    @Restrict(restriction = "account.updateAdvertisersInAgency", parameters = "advertiserInAgency.agencyId")
    public AdvertisingAccount updateAdvertiserInAgency(AdvertisingAccount advertiserInAgency) {
        Long advId = processAdvertiserAsAdmin(advertiserInAgency, OperationType.UPDATE);
        return findAdvertisingUnchecked(advId);
    }

    @Override
    @Restrict(restriction = "account.viewAdvertising")
    public List<AdvertisingAccount> findAdvertisersByAgency(Long accountId) {
        return jdbcOperations.query("select a.account_id, a.name " +
                        "from account a " +
                        "where a.agency_account_id = ? " +
                        "  and a.status != 'D'",
                new Object[]{ accountId },
                (ResultSet rs, int index) -> {
                    AdvertisingAccount advertiser = new AdvertisingAccount();
                    advertiser.setId(rs.getLong("account_id"));
                    advertiser.setName(rs.getString("name"));
                    return advertiser;
                });
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    @Restrict(restriction = "account.updateAdvertising", parameters="accountId")
    public void uploadDocument(MultipartFile file, Long accountId) {
        try {
            fileService.uploadAccountDocument(file, accountId);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    @Restrict(restriction = "account.viewAdvertisingDocuments")
    public List<String> listDocuments(Long accountId) {
        try {
            return fileService.accountDocumentsList(accountId);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @Restrict(restriction = "account.viewAdvertisingDocuments", parameters="accountId")
    public Boolean checkDocuments(Long accountId) {
        try {
            return fileService.checkAccountDocuments(Collections.singletonList(accountId))
                    .get(0);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    @Restrict(restriction = "account.viewAdvertisingDocuments", parameters="accountId")
    public byte[] downloadDocument(String name, Long accountId) {
        return fileService.downloadAccountDocument(accountId, name);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    @Restrict(restriction = "account.updateAdvertising", parameters="accountId")
    public void deleteDocument(String name, Long accountId) {
        try {
            fileService.deleteAccountDocument(accountId, name);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    private Long processAdvertiserAsAdmin(AdvertisingAccount advertiserInAgency, OperationType operationType) {
        com.foros.rs.client.model.account.Account account = new com.foros.rs.client.model.account.Account();

        if (operationType == OperationType.UPDATE) {
            account.setId(advertiserInAgency.getId());
        } else {
            account.setId(null);
        }
        account.setName(advertiserInAgency.getName());
        account.setStatus(StatusHelper.getRsStatusByDisplayStatus(advertiserInAgency.getDisplayStatus()));
        account.setAgency(ForosHelper.createEntityLink(advertiserInAgency.getAgencyId()));
        if (advertiserInAgency.getCommission() != null) {
            BigDecimal commission = advertiserInAgency.getCommission()
                                        .movePointLeft(2)
                                        .setScale(2, RoundingMode.HALF_UP);
            account.setCommission(commission);
        }

        Operation<com.foros.rs.client.model.account.Account> operation = new Operation<>();
        operation.setEntity(account);
        operation.setType(operationType);

        Operations<com.foros.rs.client.model.account.Account> operations = new Operations<>();
        operations.setOperations(Collections.singletonList(operation));

        OperationsResult result = forosService.getAdminAccountService().perform(operations);
        return result.getIds().get(0);
    }

    private static boolean isAccountDeleted(AdvertisingAccount account) {
        return getStatusMap().get(AccountDisplayStatus.DELETED.getId()).equals(account.getDisplayStatus());
    }
}
