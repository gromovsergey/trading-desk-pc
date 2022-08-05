package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.AbstractConverter;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.security.AccountRole;
import com.foros.session.account.AccountService;
import com.foros.session.security.AccountTO;
import com.foros.session.security.UserService;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.mapper.Converter;

import java.util.Collection;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

public class IdNameAccountsXmlAction extends AbstractOptionsAction<AccountTO> {
    @EJB
    private AccountService accountService;

    @EJB
    private UserService userService;

    private String[] role;
    private Long internalAccountId;
    private String[] countryCodes;

    public IdNameAccountsXmlAction() {
        super(new NamedTOConverter(true), new OptionStatusFilter(true));
    }

    @Override
    protected Collection<? extends AccountTO> getOptions() throws ProcessException {
        return EntityUtils.sortByStatus(
                accountService.search(!userService.getMyUser().isDeletedObjectsVisible(), getInternalAccountId(), countryCodes, getAccountRoles()));
    }

    @RequiredFieldValidator(key = "errors.required", message = "role")
    public String[] getRole() {
        return role;
    }

    protected AccountRole[] getAccountRoles() {
        Collection<AccountRole> accountRoles = CollectionUtils.convert(new Converter<String, AccountRole>() {
                    @Override
                    public AccountRole item(String value) {
                        return AccountRole.valueOf(value);
                    }
                }, getRole());
        return accountRoles.toArray(new AccountRole[accountRoles.size()]);
    }

    public void setRole(String[] role) {
        this.role = role;
    }

    public Long getInternalAccountId() {
        return internalAccountId;
    }

    public void setInternalAccountId(Long internalAccountId) {
        this.internalAccountId = internalAccountId;
    }

    protected AccountService getAccountService() {
        return accountService;
    }

    public String[] getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(String[] countryCodes) {
        this.countryCodes = countryCodes;
    }

    public void setConcatResultForValue(boolean concatResultForValue) {
        getConverter().setConcatForValue(concatResultForValue);
    }

    @Override
    public AbstractConverter getConverter() {
        return (AbstractConverter) super.getConverter();
    }
}
