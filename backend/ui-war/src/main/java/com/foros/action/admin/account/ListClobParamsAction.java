package com.foros.action.admin.account;

import com.foros.action.BaseActionSupport;
import com.foros.action.account.InternalAccountBreadcrumbsElement;
import com.foros.action.account.InternalAccountsBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.ClobParam;
import com.foros.model.ClobParamPK;
import com.foros.model.ClobParamType;
import com.foros.model.account.InternalAccount;
import com.foros.session.account.AccountService;
import com.foros.session.admin.ClobParamService;

import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.EJB;

public class ListClobParamsAction extends BaseActionSupport implements BreadcrumbsSupport{

    @EJB
    private ClobParamService clobParamService;

    @EJB
    private AccountService accountService;

    private Collection<ClobParam> params;

    private Long accountId;

    private InternalAccount account;

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    public String notices() {
        params = new ArrayList<ClobParam>(2);
        params.add(fetchClobParam(ClobParamType.ADVERTISER_NOTE));
        params.add(fetchClobParam(ClobParamType.PUBLISHER_NOTE));
        breadcrumbs = new Breadcrumbs().add(new InternalAccountsBreadcrumbsElement())
                .add(new InternalAccountBreadcrumbsElement(getAccount()))
                .add(new SimpleTextBreadcrumbsElement("Notice.entityName"));

        return SUCCESS;
    }

    @ReadOnly
    public String terms() {
        params = new ArrayList<ClobParam>(4);
        params.add(fetchClobParam(ClobParamType.ADV_TERMS));
        params.add(fetchClobParam(ClobParamType.PUB_TERMS));
        params.add(fetchClobParam(ClobParamType.ISP_TERMS));
        params.add(fetchClobParam(ClobParamType.CMP_TERMS));
        breadcrumbs = new Breadcrumbs().add(new InternalAccountsBreadcrumbsElement())
                .add(new InternalAccountBreadcrumbsElement(getAccount()))
                .add(new SimpleTextBreadcrumbsElement("TermsOfUse.entityName"));

        return SUCCESS;
    }

    public Collection<ClobParam> getParams() {
        return params;
    }

    private ClobParam fetchClobParam(ClobParamType type) {
        ClobParam param = clobParamService.find(accountId, type);
        if (param == null) {
            param = new ClobParam(new ClobParamPK(accountId, type));
        }
        return param;
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

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
