package com.foros.action.admin.account;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.ClobParam;
import com.foros.model.ClobParamType;
import com.foros.model.account.ExternalAccount;
import com.foros.model.account.InternalAccount;
import com.foros.session.account.AccountService;
import com.foros.session.admin.ClobParamService;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.ModelDriven;

public class DisplayNoticeAction extends BaseActionSupport implements ModelDriven<ClobParam> {

    @EJB
    private ClobParamService clobParamService;

    @EJB
    private AccountService accountService;

    private ClobParam notice;

    @ReadOnly
    public String myNotice() {
        ExternalAccount myAccount = (ExternalAccount) accountService.getMyAccount();
        ClobParamType type;
        switch (myAccount.getRole()) {
            case AGENCY:
            case ADVERTISER:
                type = ClobParamType.ADVERTISER_NOTE;
                break;
            default:
                type = ClobParamType.PUBLISHER_NOTE;
                break;
        }
        InternalAccount internal = myAccount.getInternalAccount();
        notice = clobParamService.find(internal.getId(), type);
        return SUCCESS;
    }

    @Override
    public ClobParam getModel() {
        return notice;
    }
}
