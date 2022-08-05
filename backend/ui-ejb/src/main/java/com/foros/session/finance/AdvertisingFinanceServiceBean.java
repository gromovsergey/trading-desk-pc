package com.foros.session.finance;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.PaymentOrderType;
import com.foros.model.currency.Currency;
import com.foros.model.finance.FinanceStatus;
import com.foros.model.finance.Invoice;
import com.foros.model.finance.InvoiceData;
import com.foros.model.finance.InvoicingPeriod;
import com.foros.model.security.ActionType;
import com.foros.model.security.User;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.account.AdvertisingAccountRestrictions;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.channel.exceptions.UpdateException;
import com.foros.util.DecimalUtil;
import com.foros.util.EntityUtils;
import com.foros.util.EqualsUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless(name = "AdvertisingFinanceService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class AdvertisingFinanceServiceBean extends FinanceServiceBase implements AdvertisingFinanceService {
    private static final String[] READ_ONLY_FIELDS = {
            "account",
            "totalPaid",
            "invoicedOutstanding",
            "notInvoiced"
        };

    private static final String[] RESTRICTED_FIELDS = {
            "paymentType",
            "invoiceGenerationType",
            "prepaidAmount",
            "onAccountCreditUpdated",
            "creditLimit",
            "taxRate",
            "billingFrequency",
            "billingFrequencyOffset",
            "minInvoice",
            "paymentTerms"
        };

    private static final String[] PER_CAMPAIGN_INVOICING_FIELDS = {
            "invoiceGenerationType"
        };

    private static final String[] FLAGS_FIELDS = {
            "paymentType",
            "invoiceGenerationType"
        };

    @EJB
    private CampaignService campaignService;

    @EJB
    private CurrencyExchangeService currencyExchangeService;

    @EJB
    private CurrencyService currencyService;

    @EJB
    private UserRoleService userRoleService;

    @EJB
    protected AdvertisingAccountRestrictions advertisingAccountRestrictions;

    @EJB
    protected AccountsPayableFinanceService accountsPayableFinanceService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    public void refreshInvoice(Long id) {
        Invoice invoice = viewInvoice(id);
        em.refresh(invoice);
    }

    @Override
    public Invoice findInvoiceById(Long id) {
        Invoice invoice = em.find(Invoice.class, id);
        if (invoice == null) {
            throw new EntityNotFoundException("Invoice with id=" + id + " not found");
        }
        return invoice;
    }

    @Override
    @Restrict(restriction = "Invoice.view", parameters = "find('Invoice', #id)")
    public Invoice viewInvoice(Long id) {
        return findInvoiceById(id);
    }

    @Override
    public InvoicingPeriod findInvoicingPeriodByInvoiceId(Long id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from billing.get_invoicing_period(?)", id.intValue());

        if (!rs.next()) {
            throw new EntityNotFoundException("InvoicingPeriod for invoce id=" + id + " not found");
        }

        return new InvoicingPeriod(rs.getDate(1), rs.getDate(2));
    }

    @Override
    @Restrict(restriction = "Invoice.view", parameters = "find('Invoice', #id)")
    public Invoice viewInvoiceWithWebData(Long id) {
        Invoice invoice = findInvoiceById(id);
        boolean isInputRatesAndAmountsFlag = !invoice.getAccount().getAccountType().isInputRatesAndAmountsFlag();

        for (InvoiceData invoiceData : invoice.getInvoiceDatas()) {
            BigDecimal amount = isInputRatesAndAmountsFlag ? invoiceData.getAmountNet() :
                    invoiceData.getAmountNet().add(invoiceData.getCommAmount());
            invoiceData.setAmount(amount);
        }

        return invoice;
    }

    @Override
    public AdvertisingFinancialSettings getFinancialSettings(long accountId) {
        // restriction inherited from find()
        Account account = accountService.view(accountId);
        if (!(account instanceof AdvertisingAccountBase)) {
            throw new EntityNotFoundException("Account with id=" + accountId + " not found");
        }
        return ((AdvertisingAccountBase)account).getFinancialSettings();
    }

    @Override
    @Restrict(restriction = "Invoice.update", parameters = "find('Invoice', #invoice.id)")
    @Validate(validation = "Invoice.update", parameters = "#invoice")
    public void updateInvoice(final Invoice invoice) {
        Invoice existing = findInvoiceById(invoice.getId());

        invoice.retainChanges("invoiceLegalNumber", "dueDate", "closedDate", "invoiceEmailDate", "status",
                "paidAmount", "deductFromPrepaidAmount", "creditSettlement");
        EntityUtils.copy(existing, invoice);

        try {
            callUpdateInvoice(existing);
            cacheService.evict(existing);
            em.refresh(existing);

            if (existing.getAccount().isFinancialFieldsPresent()) {
                cacheService.evict(existing.getAccount().getFinancialSettings());
            } else {
                cacheService.evict(existing.getAccount().getAgency().getFinancialSettings());
            }
        } catch (UpdateException e) {
            throw new RuntimeException(e);
        }
    }

    private void callUpdateInvoice(final Invoice invoice) {
        jdbcTemplate.execute(
                "select billing.payAdvInvoice(" +
                        "?::integer," +
                        "?::varchar," +
                        "?::varchar," +
                        "?::timestamp," +
                        "?::date," +
                        "?::timestamp," +
                        "?::numeric," +
                        "?::numeric," +
                        "?::numeric)",
                invoice.getId(),
                invoice.getInvoiceLegalNumber(),
                invoice.getStatus() == FinanceStatus.OPEN ? "O" : "C",
                invoice.getInvoiceEmailDate(),
                invoice.getDueDate(),
                invoice.getClosedDate(),
                invoice.getPaidAmount(),
                invoice.getCreditSettlement(),
                invoice.getDeductFromPrepaidAmount()
        );
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "AdvertisingAccount.updateFinance", parameters = "#settings")
    @Restrict(restriction = "AdvertisingAccount.updateFinance", parameters = "find('Account', #settings.id)")
    public void updateFinance(AdvertisingFinancialSettings settings) {
        AdvertisingFinancialSettings existingSettings = em.find(AdvertisingFinancialSettings.class, settings.getAccountId());
        AdvertisingAccountBase finAccount =  existingSettings.getAccount();

        prePersistFinance(settings);

        settings.unregisterChange(READ_ONLY_FIELDS);

        if (!finAccount.getAccountType().isPerCampaignInvoicingFlag()) {
            settings.unregisterChange(PER_CAMPAIGN_INVOICING_FIELDS);
        }

        AdvertisingAccountBase account = settings.getAccount();
        if (!advertisingAccountRestrictions.canUpdateRestrictedFinanceFields(account) || !account.isFinancialFieldsPresent()) {
            settings.unregisterChange(RESTRICTED_FIELDS);
        }

        if (!advertisingAccountRestrictions.canUpdateRestrictedFinanceFields(account)) {
            settings.unregisterChange("mediaHandlingFee");
        } else if (DecimalUtil.isZeroOrNull(settings.getMediaHandlingFee())) {
            settings.setMediaHandlingFee(BigDecimal.ZERO);
        }

        if (settings.getData().isChanged("prepaidAmount") && settings.getData().getPrepaidAmount() == null) {
            settings.getData().setPrepaidAmount(BigDecimal.ZERO);
        }

        if (settings.isChanged("creditLimit") && settings.getCreditLimit() == null) {
            settings.setCreditLimit(BigDecimal.ZERO);
        }

        PaymentOrderType paymentType = settings.isChanged("paymentType") ? settings.getPaymentType() : existingSettings.getPaymentType();

        if (paymentType == PaymentOrderType.PREPAY) {
            settings.setCreditLimit(BigDecimal.ZERO);
        }

        if (settings.isChanged("taxRate") && settings.getTaxRate() == null) {
            settings.setTaxRate(BigDecimal.ZERO);
        }

        if (settings.getTaxNumber() != null && "".equals(settings.getTaxNumber().trim())) {
            settings.setTaxNumber(null);
        }

        if (!existingSettings.isFrozen()) {
            boolean prepaidAmountMadePositive = settings.getData().getPrepaidAmount() != null && settings.getData().getPrepaidAmount().signum() == 1;
            boolean creditLimitMadePositive = settings.getCreditLimit() != null && settings.getCreditLimit().signum() == 1;
            boolean positiveCreditLine = prepaidAmountMadePositive || creditLimitMadePositive;
            if (positiveCreditLine) {
                settings.setFrozen(true);
            }
        }

        if (settings.isChanged("prepaidAmount") &&
                !EqualsUtil.equalsBigDecimal(existingSettings.getData().getPrepaidAmount(), settings.getData().getPrepaidAmount())) {
            settings.setOnAccountCreditUpdated(new Date());
        }

        if (settings.isChanged("commission") && (finAccount instanceof AdvertiserAccount) && finAccount.isStandalone()) {
            throw new SecurityException("Unable to set Agency Commission for standalone advertiser");
        } else {
            updateCommissionForZeroOrNull(settings);
        }

        if ((settings.getTaxRate() == null && existingSettings.getTaxRate() == null) ||
            (settings.getTaxRate() != null && existingSettings.getTaxRate() != null && settings.getTaxRate().compareTo(existingSettings.getTaxRate()) == 0)) {
            settings.unregisterChange("taxRate");
        }

        if (settings.getVersion().compareTo(existingSettings.getVersion()) == 0) {
            settings.unregisterChange("version");
        }

        settings.unregisterChange(FLAGS_FIELDS);
        settings.registerChange("flags");

        auditService.audit(finAccount, ActionType.UPDATE);
        em.merge(settings);
        displayStatusService.update(finAccount);
    }

    private void prePersistFinance(AdvertisingFinancialSettings financialSettings) {
        AdvertisingAccountBase account = em.find(AdvertisingAccountBase.class, financialSettings.getAccount().getId());
        financialSettings.setAccount(account);

        // default bill to user
        if (financialSettings.getDefaultBillToUser() != null && financialSettings.getDefaultBillToUser().getId() != null) {
            financialSettings.setDefaultBillToUser(em.getReference(User.class, financialSettings.getDefaultBillToUser().getId()));
        } else {
            financialSettings.setDefaultBillToUser(null);
        }

        checkChangesForDefaults(financialSettings, account);
    }

    @Override
    public List<Invoice> findInvoicesByAccount(Long id) {
        return em.createQuery("select i from Invoice i left join fetch i.campaign c " +
                " where i.advertiserAccount.id = :accountId " + getInvoiceStatusRestriction() +
                " order by i.id desc", Invoice.class)
                .setParameter("accountId", id)
                .getResultList();
    }

    @Override
    public List<Invoice> findInvoicesByCampaign(Long campaignId) {
        return em.createQuery("select i from Invoice i join i.campaign c " +
                " where i.campaign.id = :campaignId " + getInvoiceStatusRestriction() +
                " order by i.id desc", Invoice.class)
                .setParameter("campaignId", campaignId)
                .getResultList();
    }

    private String getInvoiceStatusRestriction() {
        if (!SecurityContext.isInternal()) {
            return " and (i.status = 'O' or i.status = 'C') ";
        } else {
            return "";
        }
    }

    @Override
    public void generateInvoice(Long id) {
        jdbcTemplate.execute("select billing.generateAdvInvoice(?::integer)", id);
        refreshInvoice(id);
    }

    @Override
    public void generateInvoicesByAccount(Account account) {
        if (account.getRole() == AccountRole.AGENCY) {
            for (EntityTO adv: accountService.findAdvertisersTOByAgency(account.getId())) {
                generateInvoicesByAccount(accountService.findAdvertiserAccount(adv.getId()));
            }
        } else {
            for (Invoice i: findInvoicesByAccount(account.getId())) {
                if (i.getStatus() == FinanceStatus.DUMMY) {
                    generateInvoice(i.getId());
                }
            }
        }
    }

    @Override
    public BigDecimal getConvertedMaxCreditLimit(Long accountId) {
        Account account = accountService.find(accountId);
        Currency usdCurrency = currencyService.getCurrencyByCode("USD");
        CurrencyConverter currencyConverter = currencyExchangeService.getCrossRate(account.getCurrency().getId(), new Date());
        User currentUser = em.find(User.class, SecurityContext.getPrincipal().getUserId());
        return currencyConverter.convert(usdCurrency.getId(), currentUser.getMaxCreditLimit());
    }

    @Override
    public BigDecimal getCreditBalance(Long id) {
        BigDecimal res = jdbcTemplate.queryForObject("select * from account.get_credit_balance(?)", BigDecimal.class, id);
        return res == null ? BigDecimal.ZERO : res;
    }

    @Override
    public boolean isCreditLimitValid(Long accountId, BigDecimal creditLimit) {
        if (creditLimit == null) {
            return true;
        }

        AdvertisingFinancialSettings financialSettings = getFinancialSettings(accountId);
        if (financialSettings != null && financialSettings.getCreditLimit() != null &&
                financialSettings.getCreditLimit().compareTo(creditLimit) == 0) {
            return true;
        }

        ApplicationPrincipal applicationPrincipal = SecurityContext.getPrincipal();
        Long currentUserRoleId = applicationPrincipal.getUserRoleId();
        if (userRoleService.isAdvertisingFinanceUser(currentUserRoleId)) {
            BigDecimal convertedMaxCreditLimit = getConvertedMaxCreditLimit(accountId);
            User currentUser = em.find(User.class, applicationPrincipal.getUserId());
            if (currentUser.getMaxCreditLimit().compareTo(BigDecimal.ZERO) == 0||
                    convertedMaxCreditLimit.compareTo(creditLimit) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method compares existing <b>minInvoice</b> and <b>billingFrequencyOffset</b> against incoming values.
     * If we set values which are equal to defaults, then no changes will come into bean. This method compares values
     * with existing and places a corresponding changes into changes list.
     *
     * @param settings finance settings to be persisted.
     * @param existingAccount persisted account, whose financial data is being checked.
     */
    protected void checkChangesForDefaults(AdvertisingFinancialSettings settings, AdvertisingAccountBase existingAccount) {
        if (accountRestrictions.isUpdateFinanceGranted(existingAccount) && accountRestrictions.canUpdate(existingAccount)) {
            AdvertisingFinancialSettings existingSettings = existingAccount.getFinancialSettings();

            if (EqualsUtil.equalsComparable(settings.getMinInvoice(), existingSettings.getMinInvoice())) {
                settings.unregisterChange("minInvoice");
            }

            if (EqualsUtil.equalsComparable(settings.getBillingFrequencyOffset(), existingSettings.getBillingFrequencyOffset())) {
                settings.unregisterChange("billingFrequencyOffset");
            }

            if (settings.getBillingFrequency() == existingSettings.getBillingFrequency()) {
                settings.unregisterChange("billingFrequency");
            }
        }
    }
}
