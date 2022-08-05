package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.IdNameEntityConverter;
import com.foros.model.IdNameEntity;
import com.foros.security.AccountRole;
import com.foros.util.helper.IndexHelper;

import java.util.Collection;

public class AccountsFilterXmlAction extends AbstractOptionsAction<IdNameEntity> {

    private AccountRole accountRole;
    private String countryCode;
    private Long agencyId;

    public AccountsFilterXmlAction() {
        super(new IdNameEntityConverter(false));
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    @Override
    protected Collection<? extends IdNameEntity> getOptions() throws ProcessException {
        if (accountRole == AccountRole.ADVERTISER && agencyId != null && agencyId != -1) {
            return IndexHelper.getAdvertisersList(agencyId);
        } else {
            return IndexHelper.getAccountsList(countryCode, accountRole);
        }
    }
}
