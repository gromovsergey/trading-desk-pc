package com.foros.action.xml.options;

import com.foros.util.helper.IndexHelper;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.IdNameEntityConverter;
import com.foros.model.IdNameEntity;
import com.foros.security.AccountRole;

import java.util.Collection;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;

public class AdvertisersXmlAction extends AbstractOptionsByAccountAction<IdNameEntity> {
    private String accountPair;

    public AdvertisersXmlAction() {
        super(new IdNameEntityConverter(true));
    }

    @AccountId
    @CustomValidator(type = "pair", key = "errors.pair", message = "value.accountPair")
    public String getAccountPair() {
        return accountPair;
    }

    public void setAccountPair(String accountPair) {
        this.accountPair = accountPair;
    }

    @Override
    protected Collection<? extends IdNameEntity> getOptionsByAccount(Long accountId) {
        if (accountId != null && accountId != -1) {
            return IndexHelper.getAdvertisersList(accountId);
        } else {
            return IndexHelper.getAccountsList(AccountRole.ADVERTISER);
        }
    }

}
