package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.model.CreditLimitInfo;
import com.foros.security.principal.SecurityContext;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import javax.ejb.EJB;

public class CreditLimitCheckXMLAction extends AbstractXmlAction<CreditLimitInfo> {
    private String creditLimit;
    private String accountId;
    @EJB
    private AdvertisingFinanceService advertisingFinanceService;
    @EJB
    private UserRoleService userRoleService;

    @Override
    protected CreditLimitInfo generateModel() throws ProcessException {
        BigDecimal creditLimitNumeric;

        try {
            creditLimitNumeric = NumberUtil.parseBigDecimal(creditLimit);
        } catch (ParseException e) {
            // exception means there is a problem of parsing so avoid doing anything further
            return new CreditLimitInfo("OK");
        }

        if (creditLimitNumeric == null
                || !userRoleService.isAdvertisingFinanceUser(SecurityContext.getPrincipal().getUserRoleId())
                || advertisingFinanceService.isCreditLimitValid(StringUtil.toLong(accountId), creditLimitNumeric)) {
            return new CreditLimitInfo("OK");
        }

        String maxCreditLimit = advertisingFinanceService.getConvertedMaxCreditLimit(StringUtil.toLong(accountId)).toString();
        String message = StringUtil.getLocalizedString("account.creditLimit.exceed", maxCreditLimit);
        return new CreditLimitInfo("NOTOK", maxCreditLimit, message);
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
