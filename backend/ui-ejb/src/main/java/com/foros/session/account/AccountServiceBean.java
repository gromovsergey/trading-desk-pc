package com.foros.session.account;

import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getTextAdImageFileContentRestriction;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getTextAdImageFileSizeRestriction;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getTextAdImageRestrictionFilter;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getUploadMaxAccountSizeQuotaProvider;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getUploadMaxDirLevelsRestriction;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getUploadMaxFileSizeRestriction;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getUploadMaxFilesInDir;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getUploadMaxFilesInZipRestriction;
import com.foros.cache.NamedCO;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.ConfigService;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.Timezone;
import com.foros.model.account.Account;
import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.account.AccountFinancialSettings;
import com.foros.model.account.AccountSearchTestOption;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.ExternalAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.account.TnsAdvertiser;
import com.foros.model.campaign.CampaignType;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.currency.Currency;
import com.foros.model.fileman.FileInfo;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.security.AccountType;
import com.foros.model.security.ActionType;
import com.foros.model.security.TextAdservingMode;
import com.foros.model.security.User;
import com.foros.model.template.OptionValueUtils;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.BusinessException;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.NamedTO;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.TreeFilterElementTOConverter;
import com.foros.session.account.AccountSelector.Builder;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.FileManager;
import com.foros.session.fileman.FileManagerImpl;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.fileman.restrictions.CompositeFileRestriction;
import com.foros.session.fileman.restrictions.RestrictionFilter;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.session.security.AccountStatsTO;
import com.foros.session.security.AccountStatsTO.AccountStatsTOBuilder;
import com.foros.session.security.AccountTO;
import com.foros.session.security.AuditService;
import com.foros.session.security.ExtensionAccountTO;
import com.foros.session.security.ManagerAccountTO;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.status.StatusService;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.bean.Filter;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.mapper.Converter;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "AccountService")
@Interceptors({ PersistenceExceptionInterceptor.class, RestrictionInterceptor.class, ValidationInterceptor.class })
public class AccountServiceBean implements AccountService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private AuditService auditService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private StatusService statusService;

    @EJB
    private UserService userService;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private ValidationService validationService;

    @EJB
    private ConfigService config;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private AdvertisingFinanceService advertisingFinanceService;

    @EJB
    private PermissionService permissionService;

    public AccountServiceBean() {
    }

    private PathProvider getPublishersPP() {
        return pathProviderService.getPublisherAccounts();
    }

    private PathProvider getOpportunitiesPP() {
        return pathProviderService.getOpportunities();
    }

    private PathProvider getDocumentsPP() {
        return pathProviderService.getAccountDocuments();
    }

    private PathProvider getChannelReportPP() {
        return pathProviderService.getChannelReport();
    }

    private PathProvider getCreativesPP() {
        return pathProviderService.getCreatives();
    }

    private void prePersistInternal(InternalAccount account) {
        prePersist(account);

        account.unregisterChange("contractNumber");
        account.unregisterChange("contractDate");

        if (account.isChanged("advContact")) {
            if (account.getAdvContact() != null && account.getAdvContact().getId() != null) {
                account.setAdvContact(em.getReference(User.class, account.getAdvContact().getId()));
            } else {
                account.setAdvContact(null);
            }
        }

        if (account.isChanged("pubContact")) {
            if (account.getPubContact() != null && account.getPubContact().getId() != null) {
                account.setPubContact(em.getReference(User.class, account.getPubContact().getId()));
            } else {
                account.setPubContact(null);
            }
        }

        if (account.isChanged("ispContact")) {
            if (account.getIspContact() != null && account.getIspContact().getId() != null) {
                account.setIspContact(em.getReference(User.class, account.getIspContact().getId()));
            } else {
                account.setIspContact(null);
            }
        }

        if (account.isChanged("cmpContact")) {
            if (account.getCmpContact() != null && account.getCmpContact().getId() != null) {
                account.setCmpContact(em.getReference(User.class, account.getCmpContact().getId()));
            } else {
                account.setCmpContact(null);
            }
        }
    }

    private void prePersistExternal(ExternalAccount account) {
        prePersist(account);

        if (account instanceof AdvertiserAccount && !account.getCountry().getCountryCode().equalsIgnoreCase(TnsAdvertiser.COUNTRY_CODE)) {
            ((AdvertiserAccount) account).setTnsAdvertiser(null);
            ((AdvertiserAccount) account).setTnsBrand(null);
        }

        boolean isAdvertisingAccount = account instanceof AdvertisingAccountBase;
        if (isAdvertisingAccount) {
            if (!account.isSelfServiceFlag() && account.getSelfServiceCommission() != null) {
                account.setSelfServiceCommission(null);
            }
        }

        if (!isAdvertisingAccount || !((AdvertisingAccountBase)account).isStandalone()) {
            account.unregisterChange("contractNumber");
            account.unregisterChange("contractDate");
        }

        // Internal Account
        if (account.getId() == null) {
            InternalAccount internalAccount = em.find(InternalAccount.class, account.getInternalAccount().getId());
            account.setInternalAccount(internalAccount);
        }

        // account manager
        if (account.isChanged("accountManager")) {
            if (account.getAccountManager() != null && account.getAccountManager().getId() != null) {
                User accountManager = em.find(User.class, account.getAccountManager().getId());

                if (accountManager == null) {
                    throw new RuntimeException("Invalid account manager.");
                }
                account.setAccountManager(accountManager);
            } else {
                account.setAccountManager(null);
            }
        }

        if (AccountRole.CMP != account.getRole()) {
            account.setCmpContact(null);
            account.unregisterChange("cmpContact");
        }

        if (account.isChanged("cmpContact")) {
            if (account.getCmpContact() != null && account.getCmpContact().getId() != null) {
                User cmpContact = em.find(User.class, account.getCmpContact().getId());
                if (!cmpContact.getAccount().equals(account)) {
                    throw new BusinessException("Not compatible cmp contact.");
                }
                account.setCmpContact(cmpContact);
            } else {
                account.setCmpContact(null);
            }
        }

        if (account instanceof PublisherAccount) {
            PublisherAccount pubAccount = (PublisherAccount) account;
            if (!pubAccount.isUsePubPixel()) {
                pubAccount.setPubPixelOptIn(null);
                pubAccount.setPubPixelOptOut(null);
            }

            if (currentUserService.isExternal()) {
                pubAccount.unregisterChange("creativeReapproval");
            }
        }
    }

    private void prePersist(Account account) {
        Account existing = null;
        if (account.getId() == null) {
            // country
            String ccode = account.getCountry().getCountryCode();
            account.setCountry(em.find(Country.class, ccode));

            // currency
            account.setCurrency(em.find(Currency.class, account.getCurrency().getId()));

            // timezone
            account.setTimezone(em.find(Timezone.class, account.getTimezone().getId()));
        } else {
            existing = em.find(Account.class, account.getId());
            account.setCountry(existing.getCountry());
            account.unregisterChange("country");
        }

        // account type
        if (account.isChanged("accountType")) {
            AccountType accountType = em.find(AccountType.class, account.getAccountType().getId());
            account.setAccountType(accountType);
            if (existing != null) {
                // OUI-24950 close all opened invoices if flags were changed
                AccountType existingAccountType = existing.getAccountType();
                if (existingAccountType.isPerCampaignInvoicingFlag() != accountType.isPerCampaignInvoicingFlag() ||
                        existingAccountType.isInvoicingFlag() != accountType.isInvoicingFlag()) {
                    advertisingFinanceService.generateInvoicesByAccount(account);
                }
            }
        }

        if (account.getBillingAddress() != null && StringUtil.isPropertyEmpty(account.getBillingAddress().getCity())) {
            account.getBillingAddress().setCity(EMPTY_ADDRESS_CITY);
        }

        if (account.getLegalAddress() != null && StringUtil.isPropertyEmpty(account.getLegalAddress().getCity())) {
            account.getLegalAddress().setCity(EMPTY_ADDRESS_CITY);
        }
    }

    private void prePersistTextAdservingMode(AdvertisingAccountBase advAccount) {
        if (advAccount.getTextAdservingMode() == null) {
            if (AccountRole.AGENCY == advAccount.getRole())
                advAccount.setTextAdservingMode(TextAdservingMode.ONE_TEXT_AD_PER_ADVERTISER);
            else {
                advAccount.setTextAdservingMode(TextAdservingMode.ONE_TEXT_AD);
            }
        }
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "Account.createInternalAccount", parameters = "#account")
    @Restrict(restriction = "Account.create", parameters = "#account.role")
    public Long createInternalAccount(InternalAccount account) {
        account.setStatus(Status.ACTIVE);
        account.setDisplayStatus(Account.LIVE);
        account.setAdvContact(null);
        account.setPubContact(null);
        account.setIspContact(null);
        account.setCmpContact(null);

        prePersistInternal(account);
        validationService.validate("Account.internalAccountDependencies", account).throwIfHasViolations();
        validationService.validate("Account.role", account).throwIfHasViolations();

        auditService.audit(account, ActionType.CREATE);
        em.persist(account);

        Long id = account.getId();

        AccountAuctionSettings auctionSettings = new AccountAuctionSettings(id, true);
        em.persist(auctionSettings);

        return id;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "Account.createExternalAccount", parameters = "#account")
    @Restrict(restriction = "Account.create", parameters = "#account.role")
    public Long createExternalAccount(ExternalAccount account) {
        prePersistExternalAccountFinancialSettings(account);
        return createExternalAccount(account, account.getFinancialSettings());
    }

    private Long createExternalAccount(ExternalAccount account, AccountFinancialSettings financialSettings) {
        account.setStatus(Status.ACTIVE);
        account.setDisplayStatus(Account.LIVE);

        prePersistExternal(account);

        validationService.validate("Account.externalAccountDependencies", account).throwIfHasViolations();
        validationService.validate("Account.role", account).throwIfHasViolations();

        if (account instanceof AdvertisingAccountBase) {
            AdvertisingAccountBase advAccount = (AdvertisingAccountBase) account;
            prePersistTextAdservingMode(advAccount);
            prePersistCategories(advAccount);
        }

        validationService.validate("Account.textAdservingMode", account).throwIfHasViolations();

        auditService.audit(account, ActionType.CREATE);
        em.persist(account);

        Long id = account.getId();
        financialSettings.setAccountId(id);
        account.setFinancialSettings(financialSettings);
        em.persist(financialSettings);
        em.flush();

        updateDisplayStatus(account, false);
        return id;
    }

    private void prePersistExternalAccountFinancialSettings(ExternalAccount account) {
        Country country = account.getCountry();
        AccountFinancialSettings financialSettings = account.getFinancialSettings();
        financialSettings.setTaxRate(country.isVatEnabled() ? country.getDefaultVATRate() : BigDecimal.ZERO);
    }

    private void prePersistAgencyAccountFinancialSettings(
            AgencyAccount account, BigDecimal commission, BigDecimal prepaidAmount) {
        prePersistAdvertisingAccountBaseFinancialSettings(account, prepaidAmount);
        account.getFinancialSettings().setCommission(commission);
    }

    private void prePersistStandaloneAdvertiserAccountFinancialSettings(
            AdvertiserAccount account, BigDecimal prepaidAmount) {
        prePersistAdvertisingAccountBaseFinancialSettings(account, prepaidAmount);
    }

    private void prePersistAdvertisingAccountBaseFinancialSettings(
            AdvertisingAccountBase account, BigDecimal prepaidAmount) {
        prePersistExternalAccountFinancialSettings(account);
        account.getFinancialSettings().setPaymentTerms(String.valueOf(account.getCountry().getDefaultPaymentTerms()));
        account.getFinancialSettings().getData().setPrepaidAmount(prepaidAmount);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "Account.createAgencyAccount", parameters = "#account")
    @Restrict(restriction = "Account.createAgencyAccount", parameters = "#account")
    public Long createAgencyAccount(AgencyAccount account) {
        AdvertisingFinancialSettings modifiedSettings = account.getFinancialSettings();
        AdvertisingFinancialSettings initialSettings = new AdvertisingFinancialSettings();
        initialSettings.setAccount(account);
        account.setFinancialSettings(initialSettings);

        BigDecimal commission = getAgencyCommission(account, modifiedSettings);
        BigDecimal prepaidAmount = getPrepaidAmount(modifiedSettings);

        prePersistAgencyAccountFinancialSettings(account, commission, prepaidAmount);
        return createExternalAccount(account, account.getFinancialSettings());
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "Account.createStandaloneAdvertiserAccount", parameters = "#account")
    @Restrict(restriction = "Account.createStandaloneAdvertiserAccount", parameters = "#account")
    public Long createStandaloneAdvertiserAccount(AdvertiserAccount account) {
        AdvertisingFinancialSettings modifiedSettings = account.getFinancialSettings();
        AdvertisingFinancialSettings initialSettings = new AdvertisingFinancialSettings();
        initialSettings.setAccount(account);
        account.setFinancialSettings(initialSettings);

        BigDecimal prepaidAmount = getPrepaidAmount(modifiedSettings);

        prePersistStandaloneAdvertiserAccountFinancialSettings(account, prepaidAmount);
        return createExternalAccount(account, account.getFinancialSettings());
    }

    private BigDecimal getAgencyCommission(AdvertisingAccountBase account, AdvertisingFinancialSettings settings) {
        if (settings.isChanged("commission", "commissionPercent")) {
            return settings.getCommission();
        }

        return em.find(Country.class, account.getCountry().getCountryCode()).getDefaultAgencyCommission();
    }

    private BigDecimal getPrepaidAmount(AdvertisingFinancialSettings settings) {
        if (settings.getData().isChanged("prepaidAmount")) {
            return settings.getData().getPrepaidAmount();
        }

        return BigDecimal.ZERO;
    }

    private void prePersistAddress(Account account) {
        if (account.isChanged("billingAddress")) {
            account.getBillingAddress().unregisterChange("id");
            account.setBillingAddress(em.merge(account.getBillingAddress()));
        }
        if (account.isChanged("legalAddress")) {
            account.getLegalAddress().unregisterChange("id");
            account.setLegalAddress(em.merge(account.getLegalAddress()));
        }
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    @Validate(validation = "Account.updateInternalAccount", parameters = "#account")
    @Restrict(restriction = "Account.update", parameters = "find('Account', #account.id)")
    public void updateInternalAccount(InternalAccount account) {
        account.unregisterChange("id");
        account.unregisterChange("financialSettings");
        account.unregisterChange("country");
        account.unregisterChange("currency");
        account.unregisterChange("timezone");

        Account existingAccount = find(account.getId());
        prePersistInternal(account);
        prePersistAddress(account);

        validationService.validate("Account.internalAccountDependencies", account).throwIfHasViolations();
        validationService.validate("Account.role", account).throwIfHasViolations();
        validationService.validate("Account.updateContacts", existingAccount, account).throwIfHasViolations();
        validationService.validate("Account.checkAccountType", existingAccount, account).throwIfHasViolations();

        account = em.merge(account);

        auditService.audit(account, ActionType.UPDATE);
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    @Validate(validation = "Account.updateExternalAccount", parameters = "#account")
    @Restrict(restriction = "Account.update", parameters = "find('Account', #account.id)")
    public void updateExternalAccount(ExternalAccount account) {
        account.unregisterChange("id");
        account.unregisterChange("financialSettings");
        account.unregisterChange("country");
        account.unregisterChange("currency");
        account.unregisterChange("timezone");
        account.unregisterChange("internalAccount");

        if (currentUserService.isExternal()) {
            account.unregisterChange("passbackBelowFold");
        }

        ExternalAccount existingAccount = (ExternalAccount) find(account.getId());

        if (!account.isChanged("version")) {
            account.setVersion(existingAccount.getVersion());
        }

        prePersistExternal(account);

        validationService.validate("Account.externalAccountDependencies", account).throwIfHasViolations();
        validationService.validate("Account.role", account).throwIfHasViolations();

        boolean isAccountTypeChanged = account.isChanged("accountType") && !existingAccount.getAccountType().equals(account.getAccountType());

        List<Long> oldCategories = null;

        if (account instanceof AdvertisingAccountBase) {
            AdvertisingAccountBase advAccount = (AdvertisingAccountBase) account;

            if (!accountRestrictions.canUpdateBillingContactDetails(existingAccount)) {
                account.setName(existingAccount.getName());
                account.setLegalName(existingAccount.getLegalName());
            }

            AccountType accountType = account.isChanged("accountType") ? account.getAccountType()
                    : existingAccount.getAccountType();

            if (!accountType.isAllowTextAdvertisingFlag()) {
                TextAdservingMode mode = ((AdvertisingAccountBase) existingAccount).getTextAdservingMode();
                advAccount.setTextAdservingMode(mode);
            } else {
                prePersistTextAdservingMode(advAccount);
            }

            oldCategories = prePersistCategories(advAccount);
        } else if (account instanceof IspAccount && currentUserService.isExternal()) {
            account.unregisterChange("household");
        }

        validationService.validate("Account.checkAccountType", existingAccount, account).throwIfHasViolations();
        validationService.validate("Account.textAdservingMode", account).throwIfHasViolations();

        prePersistAddress(account);

        account = em.merge(account);
        em.flush();

        auditService.audit(account, ActionType.UPDATE);

        updateDisplayStatus(account, isAccountTypeChanged);
        updateCreativeCategories(account, oldCategories);
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    @Validate(validation = "Account.updateAgencyAccount", parameters = "#account")
    @Restrict(restriction = "Account.updateAgencyAccount", parameters = "find('Account', #account.id)")
    public void updateAgencyAccount(AgencyAccount account) {
        updateAgencyBasedFinancialSettings(account.getFinancialSettings());
        updateExternalAccount(account);
    }

    private void updateAgencyBasedFinancialSettings(AdvertisingFinancialSettings settings) {
        settings.retainChanges("commission", "commissionPercent", "data");
        settings.getData().retainChanges("prepaidAmount");

        advertisingFinanceService.updateFinance(settings);
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    @Validate(validation = "Account.updateStandaloneAdvertiserAccount", parameters = "#account")
    @Restrict(restriction = "Account.update", parameters = "find('Account', #account.id)")
    public void updateStandaloneAdvertiserAccount(AdvertiserAccount account) {
        account.getFinancialSettings().retainChanges("data");
        account.getFinancialSettings().getData().retainChanges("prepaidAmount");

        advertisingFinanceService.updateFinance(account.getFinancialSettings());
        updateExternalAccount(account);
    }

    @Override
    public void updateDisplayStatus(Account account, boolean recursive) {
        if (recursive && account.getRole() == AccountRole.AGENCY) {
            for (AdvertiserAccount advertiser : ((AgencyAccount) account).getAdvertisers()) {
                displayStatusService.update(advertiser);
            }
        }
        displayStatusService.update(account);
    }

    @Override
    public Account getMyAccount() {
        return em.find(Account.class, SecurityContext.getPrincipal().getAccountId());
    }

    @Override
    public Account getMyAccountWithTerms() {
        Account account = getMyAccount();
        fillTerms((ExternalAccount) account);
        return account;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Account.activate", parameters = "find('Account', #id)")
    public void activate(Long id) {
        Account account = find(id);
        statusService.activate(account);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Account.inactivate", parameters = "find('Account', #id)")
    public void inactivate(Long id) {
        Account account = find(id);
        statusService.inactivate(account);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Account.delete", parameters = "find('Account', #id)")
    public void delete(Long id) {
        Account account = find(id);
        statusService.delete(account);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Account.undelete", parameters = "find('Account', #id)")
    public void undelete(Long id) {
        Account account = find(id);
        statusService.undelete(account);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "AgencyAdvertiserAccount.activate", parameters = "find('Account', #id)")
    public void activateAgencyAdvertiser(Long id) {
        Account account = find(id);
        statusService.activate(account);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "AgencyAdvertiserAccount.inactivate", parameters = "find('Account', #id)")
    public void inactivateAgencyAdvertiser(Long id) {
        Account account = find(id);
        statusService.inactivate(account);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "AgencyAdvertiserAccount.delete", parameters = "find('Account', #id)")
    public void deleteAgencyAdvertiser(Long id) {
        Account account = find(id);
        statusService.delete(account);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "AgencyAdvertiserAccount.undelete", parameters = "find('Account', #id)")
    public void undeleteAgencyAdvertiser(Long id) {
        Account account = find(id);
        statusService.undelete(account);
    }

    @Override
    public void refresh(Long id) {
        Account account = find(id);
        em.refresh(account);
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('Account', #id)")
    public Account view(Long id) {
        Account account = findAccountInternal(Account.class, id);
        return account;
    }

    @Override
    @Restrict(restriction = "Account.update", parameters = "find('Account', #id)")
    public Account findForEdit(Long id) {
        return find(id);
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('AdvertiserAccount', #id)")
    public AdvertiserAccount viewAdvertiserAccount(Long id) {
        return findAdvertiserAccount(id);
    }

    @Override
    public AdvertiserAccount findAdvertiserAccount(Long id) {
        return findAccountInternal(AdvertiserAccount.class, id);
    }

    @Override
    public InternalAccount findInternalAccount(Long id) {
        return findAccountInternal(InternalAccount.class, id);
    }

    @Override
    public PublisherAccount findPublisherAccount(Long id) {
        return findAccountInternal(PublisherAccount.class, id);
    }

    @Override
    public IspAccount findIspAccount(Long id) {
        return findAccountInternal(IspAccount.class, id);
    }

    @Override
    public AgencyAccount findAgencyAccount(Long id) {
        return findAccountInternal(AgencyAccount.class, id);
    }

    @Override
    @Restrict(restriction = "AgencyAdvertiserAccount.view", parameters = "find('AdvertiserAccount', #id)")
    public AdvertiserAccount viewAdvertiserInAgencyAccount(Long id) {
        AdvertiserAccount advertiser = findAccountInternal(AdvertiserAccount.class, id);
        if (advertiser.getAgency() == null) {
            throw new EntityNotFoundException("Advertiser-In-Agency with id " + id + " not found!");
        }
        return advertiser;
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('Account', #id)")
    public AdvertisingAccountBase viewAgencyOrStandAloneAdvertiser(Long id) {
        AdvertisingAccountBase account = (AdvertisingAccountBase) view(id);
        if (account instanceof AdvertiserAccount) {
            if (((AdvertiserAccount) account).isInAgencyAdvertiser()) {
                throw new EntityNotFoundException("Agency or StandAlone Advertiser with id " + id + " not found!");
            }
        }
        fillTerms(account);
        return account;
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('AgencyAccount', #id)")
    public AgencyAccount viewAgencyAccount(Long id) {
        AgencyAccount account = findAccountInternal(AgencyAccount.class, id);
        fillTerms(account);
        return account;
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('PublisherAccount', #id)")
    public PublisherAccount viewPublisherAccount(Long id) {
        PublisherAccount account = findAccountInternal(PublisherAccount.class, id);
        fillTerms(account);
        return account;
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('IspAccount', #id)")
    public IspAccount viewIspAccount(Long id) {
        IspAccount account = findAccountInternal(IspAccount.class, id);
        fillTerms(account);
        return account;
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('CmpAccount', #id)")
    public CmpAccount viewCmpAccount(Long id) {
        CmpAccount account = findAccountInternal(CmpAccount.class, id);
        fillTerms(account);
        return account;
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('InternalAccount', #id)")
    public InternalAccount viewInternalAccount(Long id) {
        return findAccountInternal(InternalAccount.class, id);
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('ExternalAccount', #id)")
    public ExternalAccount viewExternalAccount(Long id) {
        ExternalAccount account = findAccountInternal(ExternalAccount.class, id);
        fillTerms(account);
        return account;
    }

    /**
     * This method is defined without any policies.
     * This behaves exactly as find(id) method, the only difference is this method doesnot expect
     * the user to have "VIEW_*" policies.
     */
    @Override
    public Account find(Long id) {
        return findAccountInternal(Account.class, id);
    }

    @Override
    public String getAccountName(Long id) {
        return getAccountName(id, false);
    }

    @Override
    public String getAccountName(Long id, boolean appendStatusSuffix) {
        Account account = findAccountInternal(Account.class, id);
        String accountName = account.getName();
        if (appendStatusSuffix) {
            accountName = EntityUtils.appendStatusSuffix(account.getName(), account.getStatus());
        }
        return accountName;
    }

    private <T extends Account> T findAccountInternal(Class<T> accountClass, Long id) {
        if (id == null) {
            throw new EntityNotFoundException("Account with id = null not found");
        }

        T account = em.find(accountClass, id);
        if (account == null) {
            throw new EntityNotFoundException("Account with id = " + id + " not found");
        }

        return account;
    }

    @Override
    public Collection<EntityTO> findAdvertisersTOByAgency(Long agencyAccountId) {
        AccountInAgencySelector.Builder builder = new AccountInAgencySelector.Builder();
        builder.agencyId(agencyAccountId);
        return new ArrayList<EntityTO>(getExtensionAccountTOByAgency(builder.build()).getEntities());
    }

    @Override
    @Validate(validation = "AccountInAgencySelector.account", parameters = "#selector")
    @Restrict(restriction = "AgencyAdvertiserAccount.viewAny", parameters = "find('AgencyAccount', #selector.agencyId)")
    public Result<ExtensionAccountTO> getExtensionAccountTOByAgency(AccountInAgencySelector selector) {
        Long agencyAccountId = selector.getAgencyId();
        if (isDeniedAccessTo(agencyAccountId)) {
            Result<ExtensionAccountTO> result = new Result<>(new ArrayList<ExtensionAccountTO>(), null);
            return result;
        }

        AgencyAccount agencyAccount = findAgencyAccount(agencyAccountId);
        StringBuilder query = new StringBuilder(
            "SELECT a.account_id as id , a.name as name , a.status as  status, a.display_status_id as display_status_id " +
                    "FROM Account a JOIN currency c using  (currency_id) " +
                    "WHERE a.agency_account_id=? and  a.role_id=1 ");
        List<Object> params = new ArrayList<>();
        params.add(agencyAccountId);

        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            query.append(" and a.status <> 'D' ");
        }

        User currentUser = userService.getMyUser();
        if (currentUser.isAdvLevelAccessFlag()) {
            query.append(" and ( a.account_id in ("
                    + " select a1.account_id from users u"
                    + "      inner join USERADVERTISER a2 on u.iser_id=a2.user_id "
                    + "      inner join ACCOUNT a1 on a2.ACCOUNT_ID=a1_.ACCOUNT_ID "
                    + "                   where   u.USER_ID= :userId"
                    + "       )"
                    + ")");
            params.add(currentUser.getId());
        }

        if (!selector.getStatuses().isEmpty()) {
            query.append("and a.status = any(?) ");
            params.add(jdbcTemplate.createArray("varchar", CollectionUtils.convert(new Converter<Status, Character>() {
                @Override
                public Character item(Status value) {
                    return value.getLetter();
                }
            }, selector.getStatuses())));
        }

        query.append(" order by lower(a.name)");
        if (selector.getPaging() != null) {
            query.append(" limit ").append(selector.getPaging().getCount())
                .append(" offset ").append(selector.getPaging().getFirst());
        }

        List<ExtensionAccountTO> sqlResult = jdbcTemplate.query(
                query.toString(),
                params.toArray(),
                new AgencyExtensionAccountTORowMapper(agencyAccount)
        );
        return new Result<>(sqlResult, selector.getPaging());
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('AgencyAccount', find('User', #id).account.id)")
    public Collection<AdvertiserAccount> findAdvertisersByAgencyUser(Long id) {
        User user = userService.find(id);
        AgencyAccount account = (AgencyAccount) user.getAccount();

        Set<AdvertiserAccount> userAdvertisers = user.getAdvertisers();
        Set<AdvertiserAccount> availableAdvertisers;
        User myUser = userService.getMyUser();

        if (SecurityContext.isAgency() && myUser.isAdvLevelAccessFlag()) {
            Set<AdvertiserAccount> myAdvertisers = myUser.getAdvertisers();

            if (!myUser.getId().equals(id)) {
                // input user id is different than currently logged in user's id, filter available advertiser list
                availableAdvertisers = filterDeletedAdvertisers(myAdvertisers, userAdvertisers);
                availableAdvertisers.addAll(userAdvertisers);
            } else {
                // input user id is same as currently logged in user id, need to retain all the advertisers
                availableAdvertisers = myAdvertisers;
            }
        } else {
            Set<AdvertiserAccount> accountAdvertisers = account.getAdvertisers();

            if (!currentUserService.getUser().isDeletedObjectsVisible()) {
                availableAdvertisers = filterDeletedAdvertisers(accountAdvertisers, userAdvertisers);
            } else {
                availableAdvertisers = accountAdvertisers;
            }
        }

        availableAdvertisers.size(); // Fetch if lazy loaded

        return availableAdvertisers;
    }

    @Override
    public List<EntityTO> getAccountUsers(Long id) {
        Query query = em.createNamedQuery("Account.findUsers").setParameter("id", id);
        @SuppressWarnings("unchecked")
        List<EntityTO> results = query.getResultList();

        return results;
    }

    @Override
    public List<EntityTO> getSalesManagers(AdvertiserAccount account) {
        InternalAccount internalAccount =
                (account.isInAgencyAdvertiser() ? account.getAgency() : account).getInternalAccount();
        return em.createQuery(
            "select new com.foros.session.security.UserByAccountTO(u.id, u.firstName, u.lastName, u.status)" +
                    " from User u " +
                    " where u.account.class = " + AccountRole.INTERNAL.ordinal() +
                    " and u.status = 'A'" +
                    " and u.account.legalName = :legalName" +
                    " and lower(u.role.name) like '%commercial%'" +
                    " order by u.firstName, u.lastName", EntityTO.class)
                .setParameter("legalName", internalAccount.getLegalName())
                .getResultList();
    }

    @Override
    @Restrict(restriction = "Context.switch", parameters = "'Advertiser'")
    public List<AccountStatsTO> searchAdvertiserAccounts(final String name, final Long accountTypeId, final Long internalAccountId,
            final String countryCode, final Long accountManagerId, final AccountSearchTestOption testOption,
            final DisplayStatus... displayStatuses) {

        if (isDeniedAccessTo(internalAccountId)) {
            return new ArrayList<>();
        }

        return new AbstractQueryBuilder() {

            @Override
            protected void prepareAccountStatsTOBuilder(ResultSet rs, AccountStatsTOBuilder accountStatsTOBuilder) throws SQLException {
                accountStatsTOBuilder
                    .withClicks(rs.getBigDecimal("clicks").longValue())
                    .withCtr(rs.getBigDecimal("ctr"))
                    .withAdvAmount(rs.getBigDecimal("adv_amount"))
                    .withIsTest(rs.getBoolean("is_test"))
                    .withUsedAmount(rs.getBigDecimal("used_amount"));

            }

            @Override
            protected String getSql() {
                return "select * from statqueries.advertiseraccountsstats(?::varchar, ?::integer, ?::character(2), ?::integer, ?::integer[], ?::integer, ?::integer[], ?::bool)";
            }

            @Override
            protected Object[] getParameters() {
                return new Object[]
                {
                        StringUtils.isEmpty(name) ? null : name,
                        accountTypeId,
                        StringUtils.isEmpty(countryCode) ? null : countryCode,
                        testOption != null ? testOption.getValue() : AccountSearchTestOption.INCLUDE.getValue(),
                        jdbcTemplate.createArray("integer", getInternalAccountIds(internalAccountId)),
                        getAccountMangerId(accountManagerId),
                        jdbcTemplate.createArray("integer", filterDisplayStatusIds(displayStatuses)),
                        Boolean.FALSE
                };
            }
        }.execute();
    }

    @Override
    @Restrict(restriction = "Context.switch", parameters = "'Advertiser'")
    public Result<ExtensionAccountTO> getAdvertiserAccounts(final String name) {
        List<AccountStatsTO> stats = searchAdvertiserAccounts(name, null, null, null, null, null, null);
        ArrayList<ExtensionAccountTO> result = new ArrayList<>(stats.size());
        for (AccountStatsTO stat : stats) {
            Account account = find(stat.getId());

            ExtensionAccountTO to = new ExtensionAccountTO();
            to.setId(stat.getId());
            to.setName(stat.getName());
            to.setStatus(account.getStatus());
            to.setDisplayStatus(stat.getDisplayStatus());
            to.setFlags(account.getFlags());
            to.setCountryCode(account.getCountry().getCountryCode());
            to.setCurrencyCode(stat.getCurrencyCode());
            to.setRole(account.getRole());

            result.add(to);
        }

        return new Result<>(result, new Paging(0, result.size()));
    }

    @Override
    @Restrict(restriction = "Context.switch", parameters = "'Publisher'")
    public List<AccountStatsTO> searchPublisherAccounts(final String name, final Long accountTypeId, final Long internalAccountId,
            final String countryCode, final Long accountManagerId, final AccountSearchTestOption testOption,
            final DisplayStatus... displayStatuses) {

        if (isDeniedAccessTo(internalAccountId)) {
            return new ArrayList<>();
        }

        return new AbstractQueryBuilder() {

            @Override
            protected void prepareAccountStatsTOBuilder(ResultSet rs, AccountStatsTOBuilder accountStatsTOBuilder) throws SQLException {
                accountStatsTOBuilder
                    .withCreditedImps(rs.getBigDecimal("credited_imps").longValue())
                    .withRequests(rs.getBigDecimal("requests").longValue())
                    .withCost(rs.getBigDecimal("pub_amount"))
                    .withIsTest(rs.getBoolean("is_test"));

            }

            @Override
            protected String getSql() {
                return "select * from statqueries.publisheraccountsstats(?::varchar, ?::integer, ?::character(2), ?::integer, ?::integer[], ?::integer, ?::integer[])";
            }

            @Override
            protected Object[] getParameters() {
                return new Object[]
                {
                        StringUtils.isEmpty(name) ? null : name,
                        accountTypeId,
                        StringUtils.isEmpty(countryCode) ? null : countryCode,
                        testOption != null ? testOption.getValue() : AccountSearchTestOption.INCLUDE.getValue(),
                        jdbcTemplate.createArray("integer", getInternalAccountIds(internalAccountId)),
                        getAccountMangerId(accountManagerId),
                        jdbcTemplate.createArray("integer", filterDisplayStatusIds(displayStatuses))
                };
            }
        }.execute();
    }

    @Override
    @Restrict(restriction = "Context.switch", parameters = "'ISP'")
    public List<AccountStatsTO> searchISPAccounts(final String name, final Long accountTypeId, final Long internalAccountId,
            final String countryCode, final Long accountManagerId, final AccountSearchTestOption testOption, final DisplayStatus... displayStatuses) {

        if (isDeniedAccessTo(internalAccountId)) {
            return new ArrayList<>();
        }

        return new AbstractQueryBuilder() {

            @Override
            protected void prepareAccountStatsTOBuilder(ResultSet rs, AccountStatsTOBuilder accountStatsTOBuilder) throws SQLException {
                accountStatsTOBuilder
                    .withCost(rs.getBigDecimal("isp_amount"))
                    .withUsers(rs.getBigDecimal("users_count").longValue())
                    .withIsTest(rs.getBoolean("is_test"));
            }

            @Override
            protected String getSql() {
                return "select * from statqueries.ispaccountstats(?::varchar, ?::integer, ?::character(2), ?::integer, ?::integer[], ?::integer, ?::integer[])";
            }

            @Override
            protected Object[] getParameters() {
                return new Object[]
                {
                        StringUtils.isEmpty(name) ? null : name,
                        accountTypeId,
                        StringUtils.isEmpty(countryCode) ? null : countryCode,
                        testOption != null ? testOption.getValue() : AccountSearchTestOption.INCLUDE.getValue(),
                        jdbcTemplate.createArray("integer", getInternalAccountIds(internalAccountId)),
                        getAccountMangerId(accountManagerId),
                        jdbcTemplate.createArray("integer", filterDisplayStatusIds(displayStatuses))
                };
            }
        }.execute();
    }

    private static class ExtensionAccountTORowMapper implements RowMapper<ExtensionAccountTO> {
        @Override
        public ExtensionAccountTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ExtensionAccountTO to = new ExtensionAccountTO();
            to.setId(rs.getLong("id"));
            to.setName(rs.getString("name"));
            to.setStatus(Status.valueOf(rs.getString("status").charAt(0)));
            to.setDisplayStatus(Account.getDisplayStatus(rs.getLong("display_status_id")));
            to.setFlags(rs.getLong("flags"));
            to.setCountryCode(rs.getString("country_code"));
            to.setCurrencyCode(rs.getString("currency_code"));
            to.setRole(AccountRole.valueOf((int) rs.getLong("role_id")));
            return to;
        }
    }


    private static class AgencyExtensionAccountTORowMapper implements RowMapper<ExtensionAccountTO> {
        private final NamedTO agencyTO;
        private final AgencyAccount agency;


        public AgencyExtensionAccountTORowMapper(AgencyAccount agency) {
            this.agency = agency;
            this.agencyTO = new NamedTO(agency.getId(), agency.getName());
        }

        @Override
        public ExtensionAccountTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ExtensionAccountTO to = new ExtensionAccountTO();
            to.setId(rs.getLong("id"));
            to.setName(rs.getString("name"));
            to.setStatus(Status.valueOf(rs.getString("status").charAt(0)));
            to.setDisplayStatus(Account.getDisplayStatus(rs.getLong("display_status_id")));
            to.setAgency(agencyTO);
            to.setFlags(agency.getFlags());
            to.setCountryCode(agency.getCountry().getCountryCode());
            to.setCurrencyCode(agency.getCurrency().getCurrencyCode());
            to.setRole(AccountRole.ADVERTISER);
            return to;
        }
    }

    private abstract class AbstractQueryBuilder {

        protected abstract Object[] getParameters();

        protected abstract String getSql();

        protected RowMapper<AccountStatsTO> getRowMapper() {
            return new RowMapper<AccountStatsTO>() {

                @Override
                public AccountStatsTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AccountStatsTOBuilder accountStatsTOBuilder = AccountStatsTO.builder()
                        .withId(rs.getBigDecimal("account_id").longValue())
                        .withName(rs.getString("name"))
                        .withCurrencyCode(rs.getString("currency_code"))
                        .withDisplayStatusId(rs.getBigDecimal("display_status_id").longValue())
                        .withImps(rs.getBigDecimal("imps").longValue());
                    prepareAccountStatsTOBuilder(rs, accountStatsTOBuilder);
                    return accountStatsTOBuilder.build();
                }

            };
        }

        public final List<AccountStatsTO> execute() {
            return jdbcTemplate.withAuthContext().query(getSql(), getParameters(), getRowMapper());
        }

        protected abstract void prepareAccountStatsTOBuilder(ResultSet rs, AccountStatsTOBuilder accountStatsTOBuilder) throws SQLException;
    }

    @Override
    @Restrict(restriction = "Context.switch", parameters = "'CMP'")
    public List<AccountStatsTO> searchCMPAccounts(final String name, final Long accountTypeId, final Long internalAccountId, final String countryCode, final Long accountManagerId, final DisplayStatus... displayStatuses) {

        if (isDeniedAccessTo(internalAccountId)) {
            return new ArrayList<>();
        }

        return new AbstractQueryBuilder() {
            @Override
            protected void prepareAccountStatsTOBuilder(ResultSet rs, AccountStatsTOBuilder accountStatsTOBuilder) throws SQLException {
                accountStatsTOBuilder
                    .withClicks(rs.getBigDecimal("clicks").longValue())
                    .withCost(rs.getBigDecimal("cmp_amount"));
            }

            @Override
            protected String getSql() {
                return "select * from statqueries.cmpaccountstats(?::varchar, ?::integer, ?::character(2), ?::integer[], ?::integer, ?::integer[])";
            }

            @Override
            protected Object[] getParameters() {
                return new Object[]
                {
                        StringUtils.defaultIfEmpty(name, null),
                        accountTypeId,
                        StringUtils.defaultIfEmpty(countryCode, null),
                        jdbcTemplate.createArray("integer", getInternalAccountIds(internalAccountId)),
                        getAccountMangerId(accountManagerId),
                        jdbcTemplate.createArray("integer", filterDisplayStatusIds(displayStatuses))
                };
            }
        }.execute();
    }

    private List<Long> getInternalAccountIds(Long internalAccountId) {
        List<Long> internalAccountIds = new ArrayList<>();
        if (currentUserService.isInternalWithRestrictedAccess() && internalAccountId == null) {
            internalAccountIds.addAll(currentUserService.getAccessAccountIds());
        } else {
            if (internalAccountId != null) {
                internalAccountIds.add(internalAccountId);
            }
        }
        return internalAccountIds;
    }

    private Long getAccountMangerId(Long accountManagerId) {
        User currentUser = userService.getMyUser();
        if (currentUser.getRole().isAccountManager()) {
            return currentUser.getId();
        }
        return accountManagerId;
    }

    private List<Long> filterDisplayStatusIds(DisplayStatus[] displayStatuses) {
        List<Long> displayStatusIds = new ArrayList<Long>();
        if (displayStatuses != null) {
            for (DisplayStatus displayStatuse : displayStatuses) {
                displayStatusIds.add(displayStatuse == null ? null : displayStatuse.getId());
            }
        }
        return displayStatusIds;
    }

    @Override
    public List<AccountTO> search(AccountRole... roles) {
        return search(!userService.getMyUser().isDeletedObjectsVisible(), roles);
    }

    @Override
    public List<AccountTO> searchByRoleAndTypeFlags(AccountRole role, long accountTypeFlag) {
        StringBuilder queryString = new StringBuilder("SELECT  ");
        queryString.append(" NEW com.foros.session.security.AccountTO(a.id, a.name, a.status, a.flags) ");
        queryString.append(" FROM ExternalAccount a ");
        queryString.append(" WHERE bitand(a.accountType.flags, :bitNumber) <> 0 ");
        queryString.append(" AND a.role = :role");
        if (currentUserService.isInternalWithRestrictedAccess()) {
            queryString.append(" AND a.internalAccount.id in (:accessAccountIds) ");
        }
        User currentUser = userService.getMyUser();
        if (currentUser.getRole().isAccountManager()) {
            queryString.append(" AND a.accountManager.id = :accountManagerId");
        }
        if (!currentUser.isDeletedObjectsVisible()) {
            queryString.append(" AND a.status <> 'D'");
        }

        Query query = em.createQuery(queryString.toString());
        query.setParameter("bitNumber", accountTypeFlag);
        query.setParameter("role", role);
        if (currentUser.getRole().isAccountManager()) {
            query.setParameter("accountManagerId", currentUser.getId());
        }

        if (currentUserService.isInternalWithRestrictedAccess()) {
            query.setParameter("accessAccountIds", currentUserService.getAccessAccountIds());
        }

        @SuppressWarnings("unchecked")
        List<AccountTO> accounts = query.getResultList();
        Collections.sort(accounts, new StatusNameTOComparator<AccountTO>());
        return accounts;
    }

    @Override
    public List<AccountTO> search(boolean excludeDeleted, AccountRole... roles) {
        return search(excludeDeleted, null, null, roles);
    }

    @Override
    public List<AccountTO> search(boolean excludeDeleted, Long intAccId, String[] countryCodes, AccountRole... roles) {
        Builder builder = new AccountSelector.Builder();
        if (excludeDeleted) {
            builder.excludedStatuses(Status.DELETED);
        }
        builder.internalAccountId(intAccId);
        if (countryCodes != null) {
            builder.countryCodes(countryCodes);
        }
        if (roles != null) {
            builder.roles(roles);
        }

        return search(builder.build());
    }

    public List<AccountTO> search(AccountSelector accountSelector) {
        return new ArrayList<AccountTO>(get(accountSelector, false).getEntities());
    }

    @Override
    @Validate(validation = "AccountSelector.account", parameters = "#selector")
    @Restrict(restriction = "Account.searchAccountApi")
    public Result<ExtensionAccountTO> get(AccountSelector selector) {
        return get(selector, true);
    }

    public Result<ExtensionAccountTO> get(AccountSelector selector, boolean checkRoleAccess) {
        Long intAccId = selector.getInternalAccountId();

        if (isDeniedAccessTo(intAccId)) {
            return new Result<>(new ArrayList<ExtensionAccountTO>(), selector.getPaging());
        }

        Collection<Integer> roleIds = getRoleIdsToSearch(selector, checkRoleAccess);
        if (roleIds != null && roleIds.isEmpty()) {
            return new Result<>(new ArrayList<ExtensionAccountTO>(), selector.getPaging());
        }

        StringBuilder query = new StringBuilder(
            "SELECT a.account_id as id , a.name as name , a.status as  status, " +
                    "    a.role_id as role_id, a.country_code  as country_code , a.display_status_id as display_status_id, a.flags as  flags,"
                    + "  c.currency_code as  currency_code " +
                    "FROM Account a JOIN currency c using  (currency_id) " +
                    "WHERE a.agency_account_id is null   ");
        ArrayList<Object> params = new ArrayList<>();

        if (!selector.getExcludedStatuses().isEmpty()) {
            query.append("and a.status <> all(?) ");
            params.add(jdbcTemplate.createArray("varchar", CollectionUtils.convert(new Converter<Status, Character>() {
                @Override
                public Character item(Status value) {
                    return value.getLetter();
                }
            }, selector.getExcludedStatuses())));

        }

        if (currentUserService.isInternalWithRestrictedAccess() && intAccId == null) {
            query.append(" and ( a.INTERNAL_ACCOUNT_ID = any(?) ");
            params.add(jdbcTemplate.createArray("integer", currentUserService.getAccessAccountIds()));

            query.append(" or a.account_id = any(?) )");
            params.add(jdbcTemplate.createArray("integer", currentUserService.getAccessAccountIds()));
        } else if (intAccId != null) {
            query.append(" and  a.INTERNAL_ACCOUNT_ID = ? ");
            params.add(intAccId);
        }

        if (roleIds != null) {
            query.append(" and a.role_id = any(?) ");
            params.add(jdbcTemplate.createArray("integer", roleIds));
        }

        User currentUser = userService.getMyUser();
        if (currentUser.getRole().isAccountManager()) {
            query.append(" and (a.account_manager_id = ? or a.role_id = 0) ");
            params.add(currentUser.getId());
        }

        if (!selector.getCountryCodes().isEmpty()) {
            query.append(" and country_code  = any(?) ");
            params.add(jdbcTemplate.createArray("varchar", selector.getCountryCodes()));
        }

        if (!selector.getAccountIds().isEmpty()) {
            query.append(" and ( a.account_id = any(?) ) ");
            params.add(jdbcTemplate.createArray("integer", selector.getAccountIds()));
        }

        query.append(" order by lower(name) ");

        if (selector.getPaging() != null) {
            query.append(" limit ").append(selector.getPaging().getCount())
                .append(" offset ").append(selector.getPaging().getFirst());
        }

        List<ExtensionAccountTO> result = jdbcTemplate.query(query.toString(), params.toArray(), new ExtensionAccountTORowMapper());

        return new Result<>(result, selector.getPaging());
    }

    private Collection<Integer> getRoleIdsToSearch(AccountSelector selector, boolean checkRoleAccess) {
        List<AccountRole> allRoles = Arrays.asList(AccountRole.values());
        List<AccountRole> roles;
        if (selector.getRoles().isEmpty()) {
            roles = new ArrayList<>(allRoles);
        } else {
            roles = new ArrayList<>(selector.getRoles());
        }

        if (checkRoleAccess) {
            CollectionUtils.filter(roles, new Filter<AccountRole>() {
                @Override
                public boolean accept(AccountRole element) {
                    return accountRestrictions.canView(element);
                }
            });
        }

        if (roles.size() == allRoles.size()) {
            // no filtration required
            return null;
        }

        return CollectionUtils.convert(roles, new Converter<AccountRole, Integer>() {
            @Override
            public Integer item(AccountRole value) {
                return value.getId();
            }
        });
    }

    @Override
    @Restrict(restriction = "Entity.access", parameters = "find('AgencyAccount', #agencyId)")
    public List<TreeFilterElementTO> searchAdvertisersWithCampaigns(Long agencyId, Boolean display) {
        if (agencyId == null) {
            return new ArrayList<TreeFilterElementTO>();
        }

        User currentUser = userService.getMyUser();
        List<Object> params = new ArrayList<>();

        StringBuilder queryString = new StringBuilder();
        queryString.append("    SELECT ");
        queryString.append("      a.account_id id, a.name, a.status, a.display_status_id, ");
        queryString.append("      EXISTS ( ");
        queryString.append("        SELECT * FROM Campaign ");
        queryString.append("        WHERE account_id = a.account_id ");
        if (display != null) {
            queryString.append("    AND campaign_type = ?::varchar ");
            params.add(display ? CampaignType.DISPLAY.getLetter() : CampaignType.TEXT.getLetter());
        }
        if (!currentUser.isDeletedObjectsVisible()) {
            queryString.append("    AND status <> 'D'");
        }
        queryString.append("      ) hasChildren ");
        queryString.append("    FROM Account a ");
        queryString.append("    WHERE a.role_id = ?::int ");
        params.add(AccountRole.ADVERTISER.getId());
        queryString.append("    AND a.agency_account_id = ?::int ");
        params.add(agencyId);
        if (currentUser.isAdvLevelAccessFlag()) {
            queryString.append("AND a.account_id IN (SELECT account_id FROM UserAdvertiser WHERE user_id = ?::int) ");
            params.add(currentUser.getId());
        }
        if (!currentUser.isDeletedObjectsVisible()) {
            queryString.append("AND a.status <> 'D'");
        }

        List<TreeFilterElementTO> result = jdbcTemplate.query(
            queryString.toString(),
            params.toArray(),
            new TreeFilterElementTOConverter(Account.displayStatusMap)
            );

        Collections.sort(result, new StatusNameTOComparator<TreeFilterElementTO>());
        return result;
    }

    @Override
    @Restrict(restriction = "Entity.access", parameters = "find('AgencyAccount', #agencyId)")
    public List<TreeFilterElementTO> searchAdvertisersBySizeTypeWithCampaigns(Long agencyId, Long sizeTypeId) {
        if (agencyId == null) {
            return new ArrayList<TreeFilterElementTO>();
        }

        User currentUser = userService.getMyUser();
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ");
        queryString.append("  a.account_id id, a.name, a.status, a.display_status_id, true hasChildren ");
        queryString.append("FROM Account a ");
        queryString.append("WHERE a.role_id = ? ");
        queryString.append("AND a.agency_account_id = ? ");

        if (currentUser.isAdvLevelAccessFlag()) {
            queryString.append(" AND a.account_id IN (SELECT account_id FROM UserAdvertiser WHERE user_id = ")
                .append(currentUser.getId()).append(" ) ");
        }
        if (!currentUser.isDeletedObjectsVisible()) {
            queryString.append(" AND a.status <> 'D'");
        }

        queryString.append(" AND EXISTS (SELECT 1 FROM Campaign c WHERE c.account_id = a.account_id " +
                (currentUser.isDeletedObjectsVisible() ? "" : " AND status <> 'D'") + " AND EXISTS (SELECT 1 FROM CampaignCreativeGroup ccg " +
                "     WHERE ccg.campaign_id = c.campaign_id AND EXISTS (SELECT 1 FROM CampaignCreative cc " +
                "          WHERE cc.ccg_id = ccg.ccg_id AND EXISTS (SELECT 1 FROM creative cr " +
                "              WHERE cr.creative_id = cc.creative_id AND EXISTS (SELECT 1 FROM CreativeSize cz " +
                "                  WHERE cz.size_type_id=? AND cr.size_id = cz.size_id)))))");

        List<TreeFilterElementTO> result = jdbcTemplate.query(
            queryString.toString(),
            new Object[] {
                    AccountRole.ADVERTISER.getId(),
                    agencyId,
                    sizeTypeId
            },
            new TreeFilterElementTOConverter(Account.displayStatusMap)
            );

        Collections.sort(result, new StatusNameTOComparator<>());
        return result;
    }

    private boolean isDeniedAccessTo(Long accountId) {
        if (accountId == null) {
            return false;
        }
        Account account = em.find(Account.class, accountId);
        account = account != null && account instanceof ExternalAccount ? ((ExternalAccount) account).getInternalAccount() : account;
        return currentUserService.isInternalWithRestrictedAccess() && !currentUserService.hasAccessTo(account);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "Account.addAdvertiser", parameters = "#advertiser")
    @Restrict(restriction = "AgencyAdvertiserAccount.create", parameters = "find('AgencyAccount', #advertiser.agency.id)")
    public Long addAdvertiser(AdvertiserAccount advertiser) {
        AgencyAccount agency = em.getReference(AgencyAccount.class, advertiser.getAgency().getId());
        advertiser.setCountry(agency.getCountry());

        AdvertisingFinancialSettings modifiedSettings = advertiser.getFinancialSettings();
        AdvertisingFinancialSettings initialSettings = new AdvertisingFinancialSettings();
        initialSettings.setAccount(advertiser);
        advertiser.setFinancialSettings(initialSettings);

        BigDecimal commission = getAgencyCommission(advertiser, modifiedSettings);
        BigDecimal prepaidAmount = getPrepaidAmount(modifiedSettings);
        prePersistAdvertiserFinancialSettings(advertiser, commission, prepaidAmount);

        return addAdvertiser(advertiser, advertiser.getFinancialSettings());
    }

    private Long addAdvertiser(AdvertiserAccount advertiser, AdvertisingFinancialSettings financialSettings) {
        AgencyAccount agency = em.getReference(AgencyAccount.class, advertiser.getAgency().getId());
        advertiser.setAgency(agency);
        advertiser.setStatus(Status.ACTIVE);
        advertiser.setDisplayStatus(Account.LIVE);
        Country country = agency.getCountry();
        advertiser.setCountry(country);
        advertiser.setCurrency(agency.getCurrency());
        advertiser.setInternalAccount(agency.getInternalAccount());
        advertiser.setTimezone(agency.getTimezone());

        prePersistCategories(advertiser);

        auditService.audit(advertiser, ActionType.CREATE);
        em.persist(advertiser);

        advertiser.setFinancialSettings(financialSettings);
        financialSettings.setAccountId(advertiser.getId());
        em.persist(financialSettings);

        em.flush();

        advertiser.getAgency().getAdvertisers().size();
        advertiser.getAgency().getAdvertisers().add(advertiser);
        List<AdvertiserAccount> advertiserList = new ArrayList<AdvertiserAccount>(advertiser.getAgency().getAdvertisers());
        Collections.sort(advertiserList, new Comparator<AdvertiserAccount>() {
            @Override
            public int compare(AdvertiserAccount o1, AdvertiserAccount o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        advertiser.getAgency().setAdvertisers(new LinkedHashSet<AdvertiserAccount>(advertiserList));
        if (SecurityContext.isAgency()) {
            User currentUser = em.find(User.class, SecurityContext.getPrincipal().getUserId());
            if (currentUser.isAdvLevelAccessFlag()) {
                currentUser.getAdvertisers().add(advertiser);
            }
        }

        // update display status
        Long id = advertiser.getId();
        updateDisplayStatus(advertiser, false);
        return id;
    }

    private void prePersistAdvertiserFinancialSettings(AdvertiserAccount advertiser,
            BigDecimal commission, BigDecimal prepaidAmount) {
        prePersistAdvertisingAccountBaseFinancialSettings(advertiser, prepaidAmount);
        advertiser.getFinancialSettings().setCommission(commission);

        AgencyAccount agency = em.getReference(AgencyAccount.class, advertiser.getAgency().getId());
        advertiser.getFinancialSettings().setMediaHandlingFee(agency.getFinancialSettings().getMediaHandlingFee());
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    @Validate(validation = "Account.updateAdvertiser", parameters = "#advertiser")
    @Restrict(restriction = "AgencyAdvertiserAccount.update", parameters = "find('AdvertiserAccount', #advertiser.id)")
    public void updateAdvertiser(AdvertiserAccount advertiser) {
        advertiser.unregisterChange("id", "agency", "accountType");

        AdvertiserAccount existingAdvertiser = em.find(AdvertiserAccount.class, advertiser.getId());
        if (!accountRestrictions.canUpdateBillingContactDetails(existingAdvertiser)) {
            advertiser.unregisterChange("billingAddress", "legalAddress");
        } else {
            prePersistAddress(advertiser);
        }

        if (!advertiser.isChanged("version")) {
            advertiser.setVersion(existingAdvertiser.getVersion());
        }

        List<Long> oldCategories = prePersistCategories(advertiser);

        updateAgencyBasedFinancialSettings(advertiser.getFinancialSettings());

        advertiser = em.merge(advertiser);
        em.flush();

        updateDisplayStatus(advertiser, false);
        auditService.audit(advertiser, ActionType.UPDATE);

        updateCreativeCategories(advertiser, oldCategories);

        List<AdvertiserAccount> advertiserList = new ArrayList<>(advertiser.getAgency().getAdvertisers());
        Collections.sort(advertiserList, new Comparator<AdvertiserAccount>() {
            @Override
            public int compare(AdvertiserAccount o1, AdvertiserAccount o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        advertiser.getAgency().setAdvertisers(new LinkedHashSet<AdvertiserAccount>(advertiserList));
    }

    @Override
    public void refreshAdvertiser(Long id) {
        AdvertiserAccount adv = em.find(AdvertiserAccount.class, id);
        em.refresh(adv);
    }

    @Override
    public AccountTO findIndex(Long id) {
        String qs =
                "SELECT NEW com.foros.session.security.AccountTO(a.id, a.name, a.status, a.role, a.country.countryCode , a.displayStatusId, a.flags) " +
                        "FROM Account a " +
                        "WHERE a.id = :id";
        Query q = em.createQuery(qs);
        q.setParameter("id", id);

        return (AccountTO) q.getSingleResult();
    }

    @Override
    public Collection<NamedCO<Long>> getTimeZoneIndex() {
        return em.createQuery("SELECT NEW com.foros.cache.NamedCO(tz.id, tz.key) FROM Timezone tz")
            .setHint("org.hibernate.cacheable", "true").getResultList();
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('Account', #accountId)")
    public List<User> findAccountUsers(Long accountId) {
        StringBuilder queryBuilder = new StringBuilder("SELECT u FROM User u WHERE u.account.id = :id");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            queryBuilder.append(" and u.status != 'D'");
        }

        Query q = em.createQuery(queryBuilder.toString());
        return q.setParameter("id", accountId).getResultList();
    }

    @Override
    public boolean getAccountTestFlag(Long id) {
        return find(id).getTestFlag();
    }

    @Override
    public Timezone getAccountTimeZone(Long id) {
        return find(id).getTimezone();
    }

    @Override
    public FileManager getOpportunitiesFileManager(Opportunity opportunity) {
        return new FileManagerImpl(createAccountFileSystem(getOpportunitiesPP(opportunity)));
    }

    @Override
    @Restrict(restriction = "Opportunity.view", parameters = "#opportunity")
    public FileManager getOpportunitiesFileManagerForView(Opportunity opportunity) {
        return getOpportunitiesFileManager(opportunity);
    }

    @Override
    @Restrict(restriction = "Opportunity.update", parameters = "#opportunity")
    public FileManager getOpportunitiesFileManagerForUpdate(Opportunity opportunity) {
        return getOpportunitiesFileManager(opportunity);
    }

    private PathProvider getOpportunitiesPP(Opportunity opportunity) {
        String accountFolderName = OptionValueUtils.getAdvertiserRoot(opportunity.getAccount());
        return getOpportunitiesPP().getNested(accountFolderName + opportunity.getId() + "/", OnNoProviderRoot.AutoCreate);
    }

    @Override
    @Restrict(restriction = "Account.viewDocuments", parameters = "find('Account', #accountId)")
    public FileManager getDocumentsFileManagerForView(Long accountId) {
        return getDocumentsFileManager(accountId);
    }

    @Override
    @Restrict(restriction = "Account.updateDocuments", parameters = "find('Account', #accountId)")
    public FileManager getDocumentsFileManagerForUpdate(Long accountId) {
        return getDocumentsFileManager(accountId);
    }

    private FileManager getDocumentsFileManager(Long accountId) {
        AdvertisingAccountBase account = findAccountInternal(AdvertisingAccountBase.class, accountId);
        String accountFolderName = OptionValueUtils.getAdvertisingRoot(account);
        PathProvider pp = getDocumentsPP().getNested(accountFolderName, OnNoProviderRoot.AutoCreate);
        return new FileManagerImpl(createAccountFileSystem(pp));
    }

    @Override
    public FileManager getChannelReportFileManager(Long accountId) {
        Account account = findAccountInternal(Account.class, accountId);
        String accountFolderName = "/" + account.getId() + "/";
        PathProvider pp = getChannelReportPP().getNested(accountFolderName, OnNoProviderRoot.AutoCreate);
        return new FileManagerImpl(createAccountFileSystem(pp));
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.createOrUpdate", parameters = "find('Account', #account.id)")
    public FileManager getCreativesFileManager(AdvertiserAccount account) {
        String accountFolderName = OptionValueUtils.getAdvertiserRoot(account);
        PathProvider pp = getCreativesPP().getNested(accountFolderName, OnNoProviderRoot.AutoCreate);

        FileSystem fs = createAccountFileSystem(pp);
        RestrictionFilter filter = getTextAdImageRestrictionFilter(config, pathProviderService.getCreatives());
        fs.setFileSizeRestriction(getTextAdImageFileSizeRestriction(config, filter));
        fs.setFileContentRestriction(getTextAdImageFileContentRestriction(config, filter));
        return new FileManagerImpl(fs);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.createOrUpdate", parameters = "find('Account', #account.id)")
    public FileManager getTextAdImagesFileManager(AdvertiserAccount account) {
        PathProvider pp = getCreativesPP().getNested(OptionValueUtils.getTextAdImagesRoot(config, account), OnNoProviderRoot.AutoCreate);
        PathProvider accountPP = getCreativesPP().getNested(OptionValueUtils.getAdvertiserRoot(account));

        FileSystem fs = pathProviderService.createFileSystem(pp);
        // Maximum image size during upload 2MB
        fs.setFileSizeRestriction(getTextAdImageFileSizeRestriction(config));

        fs.setFileRestriction(new CompositeFileRestriction(
            // no more than 1000 folders+files on each level of folder
            getUploadMaxFilesInDir(config),

            // no more than 10 folder levels within account folder
            getUploadMaxDirLevelsRestriction(config, accountPP)
            ));

        // no more than 1000 files+folders in one ZIP file
        fs.setZipRestriction(getUploadMaxFilesInZipRestriction(config));

        // admin may upload more
        if (!SecurityContext.isInternal()) {
            // Upload of new files should not be allowed if account files occupy more than 1GB
            fs.setQuotaProvider(getUploadMaxAccountSizeQuotaProvider(config, accountPP));
        }

        // File content restrictions
        fs.setFileContentRestriction(getTextAdImageFileContentRestriction(config));
        return new FileManagerImpl(fs);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.createOrUpdate", parameters = "find('Account', #account.id)")
    public FileManager getPublisherAccountFileManager(Account account) {
        String accountFolderName = account.getId().toString();
        PathProvider pp = getPublishersPP().getNested(accountFolderName, OnNoProviderRoot.AutoCreate);
        FileSystem fs = createAccountFileSystem(pp);
        return new FileManagerImpl(fs);
    }

    @Override
    @Restrict(restriction = "Account.view", parameters = "find('Account', #account.id)")
    public FileManager getTermsFileManager(Account account) {
        FileSystem fs = createAccountFileSystem(getAccountTermsPP(account));
        return new FileManagerImpl(fs);
    }

    private PathProvider getAccountTermsPP(Account account) {
        String accountFolderName = account.getId().toString();
        PathProvider pp = pathProviderService.getTerms().getNested(accountFolderName, OnNoProviderRoot.AutoCreate);
        return pp;
    }

    @Override
    public ContentSource getTermContent(Account account, String file) {
        return getTermsFileManager(account).readFile(file);
    }

    public void fillTerms(ExternalAccount account) {
        List<FileInfo> terms;
        try {
            terms = getTermsFileManager(account).getFileList(".");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        org.apache.commons.collections.CollectionUtils.filter(terms, new Predicate() {
            @Override
            public boolean evaluate(Object obj) {
                FileInfo file = (FileInfo) obj;
                return !file.isDirectory();
            }
        });
        Collections.sort(terms, new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                return new Long(o2.getTime()).compareTo(o1.getTime());
            }
        });
        account.setTerms(new LinkedHashSet<FileInfo>(terms));
        account.unregisterChange("terms");
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Account.updateTerms", parameters = "find('Account',#account.id)")
    public void addTerm(Account account, String fileName, InputStream is) throws IOException {
        FileManager fileManager = getTermsFileManager(account);
        fileManager.createFile("", fileName, is);
        auditService.logAddTerms(account, fileName);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Account.updateTerms", parameters = "find('Account',#account.id)")
    public boolean deleteTerm(Account account, final String file) {
        PathProvider pp = getAccountTermsPP(account);
        FileManager termsFileManager = new FileManagerImpl(createAccountFileSystem(pp));
        PathProvider ppDeleted = pp.getNested("deleted", OnNoProviderRoot.AutoCreate);
        FileManager termsDeleted = new FileManagerImpl(createAccountFileSystem(ppDeleted));
        try {
            File fileToDelete = termsFileManager.getFile(file);
            if (!fileToDelete.exists()) {
                return true;
            }
            try (FileInputStream is = new FileInputStream(fileToDelete)) {
                termsDeleted.createFile("", file, is);
            }
            boolean isDeleted = fileToDelete.delete();
            if (isDeleted) {
                // FIXME change to audit through auditService.audit
                auditService.logDeleteTerm(account, file);
            }
            return isDeleted;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileSystem createAccountFileSystem(PathProvider pp) {
        FileSystem fs = pathProviderService.createFileSystem(pp);
        // Maximum file size during upload 20MB
        fs.setFileSizeRestriction(getUploadMaxFileSizeRestriction(config));
        fs.setFileRestriction(new CompositeFileRestriction(
            // no more than 1000 folders+files on each level of folder
            getUploadMaxFilesInDir(config),

            // no more than 10 folder levels within account folder
            getUploadMaxDirLevelsRestriction(config)
            ));
        // no more than 1000 files+folders in one ZIP file
        fs.setZipRestriction(getUploadMaxFilesInZipRestriction(config));
        // admin may upload more
        if (!SecurityContext.isInternal()) {
            // Upload of new files should not be allowed if account files occupy more than 1GB
            fs.setQuotaProvider(getUploadMaxAccountSizeQuotaProvider(config));
        }
        return fs;
    }

    /**
     * Checks for existence of Account Users
     *
     * @param accountId Account Id to look for Account Users
     * @return existence of Account Users
     */
    @Override
    public boolean hasUsers(Long accountId) {
        Query query = em.createQuery("select count(*) from User u where u.account.id = :accountId and u.status <> 'D' ")
            .setParameter("accountId", accountId);
        Number users = (Number) query.getSingleResult();
        return (users.intValue() > 0);
    }

    /**
     * Checks for existence of Account Advertisers
     *
     * @param accountId Account Id to look for Account Advertisers
     * @return existence of Account Users
     */
    @Override
    public boolean hasAdvertisers(Long accountId) {
        Query query = em.createQuery(" select count(*) from AdvertiserAccount a where a.agency.id = :accountId ")
            .setParameter("accountId", accountId);
        Number advertisers = (Number) query.getSingleResult();
        return (advertisers.intValue() > 0);
    }

    @Override
    public List<ManagerAccountTO> getAllChannelOwners() {
        ChannelOwnersProvider provider = new ChannelOwnersProvider() {
            @Override
            public void appendCondition(StringBuilder query, String nonRestrictAccountClasses,
                    String restrictAccountClasses, boolean accountManager, boolean internalWithRestrictedAccess) {
                if (!restrictAccountClasses.isEmpty()) {
                    query.append(" ( ").append("      a.class IN (").append(nonRestrictAccountClasses).append(" ) ")
                        .append("      OR ")
                        .append("      ( ").append("           a.class IN ( ").append(restrictAccountClasses).append(" ) ");
                    if (internalWithRestrictedAccess) {
                        query.append("      AND a.internalAccount.id in (:accountIds) ");
                        setNeedAccountIds(true);
                    }
                    if (accountManager) {
                        query.append("      AND a.accountManager.id = :accountManagerId ");
                        setNeedAccountManagerIds(true);
                    }
                    query.append("      ) ")
                        .append("  ) ");
                } else {
                    query.append(" a.class IN (").append(nonRestrictAccountClasses).append(") ");
                }
            }
        };
        return provider.getChannelOwners();
    }

    @Override
    public List<ManagerAccountTO> getChannelOwners() {
        ChannelOwnersProvider provider = new ChannelOwnersProvider() {
            @Override
            public void appendCondition(StringBuilder query, String nonRestrictAccountClasses,
                    String restrictAccountClasses, boolean accountManager, boolean internalWithRestrictedAccess) {
                query.append(" a.class IN (").append(nonRestrictAccountClasses).append(restrictAccountClasses.isEmpty() ? " " : " , " + restrictAccountClasses).append(") ");

                if (internalWithRestrictedAccess) {
                    query.append(" AND (a.id in (:accountIds) OR a.internalAccount.id in (:accountIds))");
                    setNeedAccountIds(true);
                }

                if (accountManager) {
                    query.append(" AND a.accountManager.id = :accountManagerId ");
                    setNeedAccountManagerIds(true);
                }
            }
        };
        return provider.getChannelOwners();
    }

    private abstract class ChannelOwnersProvider {

        private boolean needAccountManagerIds;
        private boolean needAccountIds;
        private String nameFilter;

        public void setNeedAccountManagerIds(boolean needAccountManagerIds) {
            this.needAccountManagerIds = needAccountManagerIds;
        }

        public void setNeedAccountIds(boolean needAccountIds) {
            this.needAccountIds = needAccountIds;
        }

        public void setNameFilter(String nameFilter) {
            this.nameFilter = nameFilter;
        }

        public List<ManagerAccountTO> getChannelOwners() {
            boolean accountManager = currentUserService.isAccountManager();
            boolean listAdvertisers = advertisingChannelRestrictions.canView(AccountRole.ADVERTISER);
            boolean listAgencies = advertisingChannelRestrictions.canView(AccountRole.AGENCY);
            boolean internalWithRestrictedAccess = currentUserService.isInternalWithRestrictedAccess();
            boolean showDeleted = userService.getMyUser().isDeletedObjectsVisible();

            String nonRestrictAccountClasses = " InternalAccount, CmpAccount";
            String restrictAccountClasses = (listAdvertisers ? " AdvertiserAccount" : "");
            restrictAccountClasses += (listAgencies ? (listAdvertisers ? ", AgencyAccount" : " AgencyAccount") : "");

            StringBuilder query = new StringBuilder("SELECT NEW com.foros.session.security.ManagerAccountTO(a.id, a.name, a.status, a.role, a.flags) FROM Account a ");
            query.append("WHERE a.agency IS NULL AND ");

            appendCondition(query, nonRestrictAccountClasses, restrictAccountClasses,
                accountManager, internalWithRestrictedAccess);

            if (!showDeleted) {
                query.append(" and a.status <> 'D' ");
            }

            if (nameFilter != null) {
                query.append("and UPPER(a.name) LIKE :nameFilter ESCAPE '\\'");
            }

            Query q = em.createQuery(query.toString());

            if (needAccountIds) {
                q.setParameter("accountIds", currentUserService.getAccessAccountIds());
            }

            if (needAccountManagerIds) {
                q.setParameter("accountManagerId", currentUserService.getUserId());
            }

            if (nameFilter != null) {
                q.setParameter("nameFilter", "%" + SQLUtil.getEscapedString(nameFilter.toUpperCase(), '\\') + "%");
            }

            @SuppressWarnings("unchecked")
            List<ManagerAccountTO> accounts = q.getResultList();

            Collections.sort(accounts, new StatusNameTOComparator<ManagerAccountTO>());
            return accounts;
        }

        public abstract void appendCondition(StringBuilder query, String nonRestrictAccountClasses,
                String restrictAccountClasses, boolean accountManager, boolean internalWithRestrictedAccess);
    }

    @Override
    public List<EntityTO> getInternalAccountsWithoutRestricted(boolean excludeDeleted) {
        return getInternalAccounts(false, excludeDeleted);
    }

    @Override
    public List<EntityTO> getInternalAccounts(boolean excludeDeleted) {
        return getInternalAccounts(true, excludeDeleted);
    }

    private List<EntityTO> getInternalAccounts(boolean isRestrictedAccess, boolean excludeDeleted) {
        StringBuilder ql = new StringBuilder();

        ql.append("SELECT a.account_id, a.name, a.status ");
        ql.append("FROM Account a ");
        ql.append("WHERE a.role_id = ").append(AccountRole.INTERNAL.getId());

        if (excludeDeleted) {
            ql.append(" and a.status <> 'D' ");
        }

        if (isRestrictedAccess && currentUserService.isInternalWithRestrictedAccess()) {
            ql.append(" and ").append(SQLUtil.formatINClause("a.account_id", currentUserService.getAccessAccountIds()));
        }

        Query q = em.createNativeQuery(ql.toString());

        @SuppressWarnings("unchecked")
        List<Object[]> sqlResult = q.getResultList();
        List<EntityTO> result = new ArrayList<EntityTO>(sqlResult.size());
        for (Object[] row : sqlResult) {
            result.add(new EntityTO(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (Character) row[2]));
        }

        Collections.sort(result, new IdNameComparator());
        return result;
    }

    @Override
    @Restrict(restriction = "Context.switch", parameters = "find('Account', #accountId)")
    public <T extends Account> T findForSwitching(Class<T> expectedClass, Long accountId) {
        return findAccountInternal(expectedClass, accountId);
    }

    private Set<AdvertiserAccount> filterDeletedAdvertisers(Set<AdvertiserAccount> myAdvertisers, final Set<AdvertiserAccount> userAdvertisers) {
        Set<AdvertiserAccount> filtered = new LinkedHashSet<AdvertiserAccount>(myAdvertisers);

        CollectionUtils.filter(filtered, new Filter<AdvertiserAccount>() {
            @Override
            public boolean accept(AdvertiserAccount advertiser) {
                return advertiser.getStatus() != Status.DELETED || userAdvertisers.contains(advertiser);
            }
        });

        return filtered;
    }

    @Override
    public List<TreeFilterElementTO> searchAdvertisersWithConversions(Long agencyId) {
        if (agencyId == null) {
            return new ArrayList<TreeFilterElementTO>();
        }

        User currentUser = userService.getMyUser();
        List<Object> params = new ArrayList<>();

        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ");
        queryString.append("  a.account_id id, a.name, a.status, a.display_status_id ");
        queryString.append("  FROM Account a ");
        queryString.append(" WHERE a.role_id = ?::int ");
        params.add(AccountRole.ADVERTISER.getId());
        queryString.append(" AND a.agency_account_id = ?::int ");
        params.add(agencyId);
        queryString.append(" AND EXISTS ( SELECT * FROM Action WHERE account_id = a.account_id ) ");
        if (currentUser.isAdvLevelAccessFlag()) {
            queryString.append(" AND a.account_id IN (SELECT account_id FROM UserAdvertiser WHERE user_id = ?::int) ");
            params.add(currentUser.getId());
        }
        if (!currentUser.isDeletedObjectsVisible()) {
            queryString.append(" AND a.status <> 'D'");
        }

        List<TreeFilterElementTO> result = jdbcTemplate.query(
            queryString.toString(),
            params.toArray(),
            new TreeFilterElementTOConverter(Account.displayStatusMap, true)
            );

        Collections.sort(result, new StatusNameTOComparator<TreeFilterElementTO>());
        return result;
    }

    @Override
    public Set<CreativeCategory> loadCategories(Account account) {
        if (account.getId() == null || !(account instanceof AdvertiserAccount)) {
            return Collections.emptySet();
        }
        String ql = "select elements(a.categories) from AdvertiserAccount a where a.id=:id";
        List<CreativeCategory> list = em.createQuery(ql, CreativeCategory.class)
            .setParameter("id", account.getId()).getResultList();
        return new HashSet<>(list);
    }

    private List<Long> prePersistCategories(AdvertisingAccountBase account) {
        if (!(account instanceof AdvertiserAccount) || !account.isChanged("categories")) {
            return null;
        }
        AdvertiserAccount advertiser = (AdvertiserAccount) account;
        Set<CreativeCategory> existingCategories = loadCategories(advertiser);
        if (ObjectUtils.equals(advertiser.getCategories(), existingCategories)) {
            account.unregisterChange("categories");
            return null;
        }
        Set<CreativeCategory> managedCategories = new LinkedHashSet<>();
        for (CreativeCategory cc : advertiser.getCategories()) {
            managedCategories.add(em.getReference(CreativeCategory.class, cc.getId()));
        }
        advertiser.setCategories(managedCategories);
        return CollectionUtils.convert(existingCategories, new Converter<CreativeCategory, Long>() {
            @Override
            public Long item(CreativeCategory value) {
                return value.getId();
            }
        });
    }

    private void updateCreativeCategories(ExternalAccount account, List<Long> oldIds) {
        if (account instanceof AdvertiserAccount
                && account.isChanged("categories") && oldIds != null
                && !((AdvertiserAccount) account).getCategories().isEmpty()) {
            jdbcTemplate.execute(
                "select exclusions.set_default_content_categories_on_creatives(?::int,?::int[])",
                account.getId(),
                jdbcTemplate.createArray("int", oldIds)
                );
            jdbcTemplate.scheduleEviction();
        }
    }
}
