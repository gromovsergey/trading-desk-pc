package com.foros.action.admin.account;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.model.ClobParam;
import com.foros.model.ClobParamType;
import com.foros.model.account.InternalAccount;
import com.foros.session.account.AccountService;
import com.foros.session.admin.ClobParamService;

import javax.ejb.EJB;

public class ClobParamActionSupport extends BaseActionSupport implements ModelDriven<ClobParam> {

    @EJB
    ClobParamService clobParamService;

    @EJB
    private AccountService accountService;

    ClobParam param = new ClobParam();
    ClobParamType type;
    Long accountId;
    private InternalAccount account;

    @Override
    public ClobParam getModel() {
        return param;
    }

    public ClobParamType getType() {
        return type;
    }

    public void setType(ClobParamType type) {
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public InternalAccount getAccount() {
        if (account == null) {
            account = accountService.findInternalAccount(accountId);
        }
        return account;
    }
}
