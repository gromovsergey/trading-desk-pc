package com.foros.util;

import com.foros.action.account.ContextNotSetException;
import com.foros.model.account.Account;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.util.context.AdvertiserContext;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;

public class AccountUtil {
    /**
     * Fetch account id of the current user from SecurityContext.
     * @return Fetched account id.
     */
    public static Long getMyAccountId() {
        return SecurityContext.getPrincipal().getAccountId();
    }

    /**
     * Fetch account with accordant accountId.
     * @param accountId Account id.
     * @return Fetched account
     */
    public static Account extractAccount(Long accountId) {
        return extractAccountById(accountId);
    }

    /**
     * Fetch account with accordant accountId.
     * @param accountId Account id.
     * @return Fetched account
     */
    public static Account extractAccount(String accountId) {
        return extractAccountById(StringUtil.toLong(accountId));
    }


    public static Account extractAccountById(Long accountId) {
        AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
        return accountService.find(accountId);
    }

    private static Long processExternalAccountId(Object form, String property, AccountRole... rolesToProcess) {
        Long id = getAccountIdProperty(form, property);

        if (id == null && SecurityContext.isUserInOneOfRoles(rolesToProcess)) {
            id = SecurityContext.getPrincipal().getAccountId();
            setAccountIdProperty(form, property, id);
        }

        if (id == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }

        return id;
    }

    /**
     * Set account id of external user to the form if it isn't set already.
     * Returns form's account id (existing or set by method).
     * @param form form to set account id property
     * @param property account id property name.
     * @return form's account id.
     */
    public static Long setExternalAccountId(Object form, String property) {
        return processExternalAccountId(form, property,
                AccountRole.AGENCY, AccountRole.ADVERTISER, AccountRole.PUBLISHER,
                AccountRole.ISP, AccountRole.CMP);
    }

    public static Long setExternalAdvertiserId(Object form, String property) {
        return processExternalAccountId(form, property, AccountRole.ADVERTISER);
    }

    public static Long getExternalAdvertiserId(Long id) {
        return getExternalId(id, AccountRole.ADVERTISER);
    }

    private static Long getExternalId(Long id, AccountRole role) {
        if (id != null) {
            return id;
        }

        if (SecurityContext.isUserInRole(role)) {
            return SecurityContext.getPrincipal().getAccountId();
        }

        throw new EntityNotFoundException("Entity with id = null not found");
    }

    public static String getAccountParam(String parameterName, Long accountId) {
        if (SecurityContext.isInternal() || !getMyAccountId().equals(accountId)) {
            return parameterName + "=" + StringUtil.toString(accountId);
        }
        return "";
    }

    private static void setAccountIdProperty(Object form, String property, Long id) {
        try {
            // BeanUtils can set long value to long property as well as to string property
            org.apache.commons.beanutils.BeanUtils.setProperty(form, property, id);
        } catch (Exception e) {
            throw new RuntimeException("Can't set property " + property + " value", e);
        }
    }

    private static Long getAccountIdProperty(Object form, String property) {
        Object idObj;
        try {
            idObj = PropertyUtils.getProperty(form, property);
        } catch (Exception e) {
            throw new RuntimeException("Can't get " + property + " value", e);
        }

        if (idObj == null) {
            return null;
        }

        if (idObj instanceof Long) {
            return (Long) idObj;
        } else if (idObj instanceof String) {
            return StringUtil.toLong((String) idObj);
        } else {
            throw new RuntimeException("Can't convert " + idObj.getClass() + " to id");
        }
    }

    public static void populateAccountVATOptions(HttpServletRequest request, Account account) {
        request.setAttribute("VATEnabled", account.getCountry().isVatEnabled());
        request.setAttribute("VATNumberInputEnabled", account.getCountry().isVatEnabled() && account.getCountry().isVatNumberInputEnabled());
    }

    public static void validateAgencyAdvertiserContext(AdvertiserContext advContext, String accountId) {
        if (accountId == null && advContext.isAgencyContext() && !advContext.isAdvertiserSet()) {
            throw new ContextNotSetException("error.context.notset.advertiser");
        }
    }
}
