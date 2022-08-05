package com.foros.action.admin.account;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.ClobParam;
import com.foros.model.ClobParamType;
import com.foros.model.account.ExternalAccount;
import com.foros.session.account.AccountService;
import com.foros.session.admin.ClobParamService;

import javax.ejb.EJB;

public class ViewTermsAction extends BaseActionSupport implements ModelDriven<ClobParam> {

    @EJB
    private AccountService accountService;

    @EJB
    private ClobParamService clobParamService;

    private ClobParam terms;

    @ReadOnly
    public String view() {
        ExternalAccount account = (ExternalAccount) accountService.getMyAccount();
        ClobParamType type;
        switch (account.getRole()) {
            case AGENCY:
            case ADVERTISER:
                type = ClobParamType.ADV_TERMS;
                break;
            case PUBLISHER:
                type = ClobParamType.PUB_TERMS;
                break;
            case ISP:
                type = ClobParamType.ISP_TERMS;
                break;
            default:
                type = ClobParamType.CMP_TERMS;
                break;
        }
        Long internalAccountId = account.getInternalAccount().getId();
        terms = clobParamService.find(internalAccountId, type);
        return SUCCESS;
    }

    @Override
    public ClobParam getModel() {
        return terms;
    }
}
