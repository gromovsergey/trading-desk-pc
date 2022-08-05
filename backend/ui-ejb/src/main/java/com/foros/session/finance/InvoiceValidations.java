package com.foros.session.finance;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.finance.FinanceStatus;
import com.foros.model.finance.Invoice;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.constraint.validator.RequiredValidator;
import com.foros.validation.strategy.ValidationMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@LocalBean
@Stateless
@Validations
public class InvoiceValidations {
    private static final BigDecimal MAX_PRECISION_LIMIT = new BigDecimal("1000000000");

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AdvertisingFinanceService financeService;

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) Invoice invoice) {
        Invoice existingInvoice = financeService.findInvoiceById(invoice.getId());
        int maxFractionDigits = existingInvoice.getAccount().getCurrency().getFractionDigits();

        // Invoice Legal number
        if (!isLegalNumberUnique(invoice, existingInvoice.getAccount())) {
            context.addConstraintViolation("errors.invoice.duplicateLegalNumber").withPath("invoiceLegalNumber");
        }

        // Paid
        if (invoice.getPaidAmount() != null) {
            context.validator(FractionDigitsValidator.class)
                    .withFraction(maxFractionDigits)
                    .withPath("paidAmount")
                    .validate(invoice.getPaidAmount());
            context.validator(RangeValidator.class)
                    .withMin(MAX_PRECISION_LIMIT.negate(), maxFractionDigits)
                    .withMax(MAX_PRECISION_LIMIT, maxFractionDigits)
                    .withPath("paidAmount")
                    .validate(invoice.getPaidAmount());
        }

        // Credit Note Negotiated Settlement
        if (invoice.getCreditSettlement() != null) {
            context.validator(FractionDigitsValidator.class)
                    .withFraction(maxFractionDigits)
                    .withPath("creditSettlement")
                    .validate(invoice.getCreditSettlement());
            context.validator(RangeValidator.class)
                    .withMin(MAX_PRECISION_LIMIT.negate(), maxFractionDigits)
                    .withMax(MAX_PRECISION_LIMIT, maxFractionDigits)
                    .withPath("creditSettlement")
                    .validate(invoice.getCreditSettlement());
        }

        // Deduct from Prepaid Amount
        BigDecimal deductFromPrepaidAmount = invoice.getDeductFromPrepaidAmount() == null ?
                BigDecimal.ZERO : invoice.getDeductFromPrepaidAmount();
        context.validator(FractionDigitsValidator.class)
                .withFraction(maxFractionDigits)
                .withPath("deductFromPrepaidAmount")
                .validate(deductFromPrepaidAmount);

        BigDecimal accountPrepaidAmount;
        if (existingInvoice.getAccount().isFinancialFieldsPresent()) {
            accountPrepaidAmount = existingInvoice.getAccount().getFinancialSettings().getData().getPrepaidAmount() == null ?
                    BigDecimal.ZERO : existingInvoice.getAccount().getFinancialSettings().getData().getPrepaidAmount();
        } else {
            accountPrepaidAmount = existingInvoice.getAccount().getAgency().getFinancialSettings().getData().getPrepaidAmount() == null ?
                    BigDecimal.ZERO : existingInvoice.getAccount().getAgency().getFinancialSettings().getData().getPrepaidAmount();
        }

        if (deductFromPrepaidAmount.compareTo(accountPrepaidAmount.add(existingInvoice.getDeductFromPrepaidAmount())) > 0) {
            context.addConstraintViolation("errors.invoice.invalidDeductedFromPrepaidAmount")
                    .withPath("deductFromPrepaidAmount");
        }

        context.validator(RangeValidator.class)
                .withMin(BigDecimal.ZERO)
                .withPath("deductFromPrepaidAmount")
                .validate(deductFromPrepaidAmount);

        // Total Amount Payable
        if (context.props("totalAmountDue").reachableAndNoViolations()) {
            BigDecimal totalAmountDue = invoice.getTotalAmountDue();
            BigDecimal existingTotalAmount = existingInvoice.getTotalAmountDue().setScale(maxFractionDigits, RoundingMode.HALF_UP);
            if (totalAmountDue == null || existingTotalAmount.compareTo(totalAmountDue) != 0) {
                context.addConstraintViolation("errors.invoice.invalidAmountPayable").withPath("totalAmountDue");
            } else {
                context.validator(FractionDigitsValidator.class)
                        .withFraction(maxFractionDigits)
                        .withPath("totalAmountDue")
                        .validate(totalAmountDue);
            }
        }

        // Closed Date
        if (invoice.getStatus() == FinanceStatus.CLOSED) {
            context.validator(RequiredValidator.class)
                    .withMessage("errors.field.required")
                    .withPath("closedDate")
                    .validate(invoice.getClosedDate());
            if (context.props("totalAmountDue", "paidAmount", "deductFromPrepaidAmount", "creditSettlement").all().reachableAndNoViolations() &&
                invoice.getPaidAmount() != null && invoice.getCreditSettlement() != null) {
                BigDecimal paidAmountCalculated = invoice.getPaidAmount().add(deductFromPrepaidAmount)
                        .add(invoice.getCreditSettlement()).setScale(maxFractionDigits, RoundingMode.HALF_UP);
                if (invoice.getTotalAmountDue().subtract(paidAmountCalculated).signum() != 0) {
                    context.addConstraintViolation("errors.invoice.unableToSaveClose").withPath("status");
                }
            }
        }
    }

    private boolean isLegalNumberUnique(Invoice invoice, AdvertiserAccount invoiceAccount) {
        if (StringUtil.isPropertyEmpty(invoice.getInvoiceLegalNumber())) {
            return true;
        }

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" SELECT COUNT(i.INVOICE_ID)");
        queryBuilder.append(" FROM BILLING.INVOICE i ");
        queryBuilder.append("   INNER JOIN ACCOUNT a ON i.ADV_ACCOUNT_ID = a.ACCOUNT_ID");
        queryBuilder.append("    AND a.INTERNAL_ACCOUNT_ID = :internalAccountId ");
        queryBuilder.append(" WHERE UPPER(i.INVOICE_LEGAL_NUMBER) = UPPER(:legalNumber)");
        queryBuilder.append(" AND i.INVOICE_ID <> :invoiceId");

        Query query = em.createNativeQuery(queryBuilder.toString())
                .setParameter("internalAccountId", invoiceAccount.getInternalAccount().getId())
                .setParameter("legalNumber", invoice.getInvoiceLegalNumber())
                .setParameter("invoiceId", invoice.getId());

        return ((Number) query.getSingleResult()).intValue() == 0;
    }
}
