package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.Country;
import com.foros.model.currency.Currency;
import com.foros.model.security.PaymentMethod;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;
import com.foros.validation.constraint.ValuesConstraint;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.hibernate.annotations.Type;

@Entity
@DiscriminatorValue("P")
@NamedQueries({
        @NamedQuery(name = "AccountsPayableFinancialSettings.findByAccountId", query =
           "SELECT f FROM AccountsPayableFinancialSettings f WHERE f.account.id = :accountId")
})
public class AccountsPayableFinancialSettings extends AccountFinancialSettings {

    @RequiredConstraint
    @ValuesConstraint(values = {"BACS", "Swift"})
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD")
    private PaymentMethod paymentMethod;

    @RequiredConstraint
    @HasIdConstraint
    @JoinColumn(name = "BANK_COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE")
    @ManyToOne
    private Country bankCountry;

    @StringSizeConstraint(size = 15)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_SORT_CODE")
    @Type(type = "com.foros.persistence.hibernate.type.EmptyToNullStringType")
    private String bankSortCode;

    @RequiredConstraint
    @ByteLengthConstraint(length = 80)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_NAME")
    private String bankName;

    @RequiredConstraint
    @ByteLengthConstraint(length = 80)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_BRANCH_NAME")
    private String bankBranchName;

    @RequiredConstraint
    @ByteLengthConstraint(length = 100)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_ACCOUNT_NUMBER")
    private String bankAccountNumber;

    @RequiredConstraint
    @ByteLengthConstraint(length = 80)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_ACCOUNT_NAME")
    private String bankAccountName;

    @RequiredConstraint
    @HasIdConstraint
    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "BANK_ACCOUNT_CURRENCY_ID", referencedColumnName = "CURRENCY_ID")
    @ManyToOne
    private Currency bankCurrency;

    @StringSizeConstraint(size = 50)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_ACCOUNT_IBAN")
    @Type(type = "com.foros.persistence.hibernate.type.EmptyToNullStringType")
    private String bankAccountIban;

    @StringSizeConstraint(size = 30)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_ACCOUNT_CHECK_DIGITS")
    private String bankAccountCheckDigits;

    @StringSizeConstraint(size = 30)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_NUMBER")
    private String bankNumber;

    @StringSizeConstraint(size = 30)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_BIC_CODE")
    @Type(type = "com.foros.persistence.hibernate.type.EmptyToNullStringType")
    private String bankBicCode;

    @StringSizeConstraint(size = 25)
    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "BANK_ACCOUNT_TYPE")
    private String bankAccountType;

    @Override
    public AccountsPayableAccountBase getAccount() {
        return (AccountsPayableAccountBase) super.getAccount();
    }

    public void setAccount(AccountsPayableAccountBase account) {
        super.setAccount(account);
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.registerChange("paymentMethod");
    }

    public Country getBankCountry() {
        return bankCountry;
    }

    public void setBankCountry(Country bankCountry) {
        this.bankCountry = bankCountry;
        this.registerChange("bankCountry");
    }

    public String getBankSortCode() {
        return bankSortCode;
    }

    public void setBankSortCode(String bankSortCode) {
        this.bankSortCode = bankSortCode;
        this.registerChange("bankSortCode");
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
        this.registerChange("bankName");
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
        this.registerChange("bankBranchName");
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
        this.registerChange("bankAccountNumber");
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
        this.registerChange("bankAccountName");
    }

    public Currency getBankCurrency() {
        return bankCurrency;
    }

    public void setBankCurrency(Currency bankCurrency) {
        this.bankCurrency = bankCurrency;
        this.registerChange("bankCurrency");
    }

    public String getBankAccountIban() {
        return bankAccountIban;
    }

    public void setBankAccountIban(String bankAccountIban) {
        this.bankAccountIban = bankAccountIban;
        this.registerChange("bankAccountIban");
    }

    public String getBankAccountCheckDigits() {
        return bankAccountCheckDigits;
    }

    public void setBankAccountCheckDigits(String bankAccountCheckDigits) {
        this.bankAccountCheckDigits = bankAccountCheckDigits;
        this.registerChange("bankAccountCheckDigits");
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
        this.registerChange("bankNumber");
    }

    public String getBankBicCode() {
        return bankBicCode;
    }

    public void setBankBicCode(String bankBicCode) {
        this.bankBicCode = bankBicCode;
        this.registerChange("bankBicCode");
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
        this.registerChange("bankAccountType");
    }

    public String getBankAccountNumberStripped() {
        if (bankAccountNumber != null && bankAccountNumber.length() > 2) {
            char[] asChars = bankAccountNumber.toCharArray();

            for (int i = 0 ; i < bankAccountNumber.length() - 2 ; i++) {
                asChars[i] = '*';
            }

            return String.valueOf(asChars);
        }

        return bankAccountNumber;
    }
}
