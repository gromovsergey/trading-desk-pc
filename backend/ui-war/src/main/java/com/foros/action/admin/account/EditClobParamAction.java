package com.foros.action.admin.account;

import com.foros.action.account.InternalAccountBreadcrumbsElement;
import com.foros.action.account.InternalAccountsBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.ClobParam;
import com.foros.model.ClobParamPK;
import com.foros.model.ClobParamType;

import java.sql.Timestamp;
import java.util.Date;

public class EditClobParamAction extends ClobParamActionSupport implements BreadcrumbsSupport {

    @ReadOnly
    public String edit() {
        param = clobParamService.find(accountId, type);
        if (param == null) {
            param = new ClobParam(new ClobParamPK(accountId, type));
            param.setVersion(new Timestamp(new Date().getTime()));
        }
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        if (type == ClobParamType.ADVERTISER_NOTE || type == ClobParamType.PUBLISHER_NOTE) {
            return new Breadcrumbs().add(new InternalAccountsBreadcrumbsElement())
                    .add(new InternalAccountBreadcrumbsElement(getAccount()))
                    .add(ClobParamBreadcrumbsElement.createNoticeBreadcrumbsElement(accountId))
                    .add(ActionBreadcrumbs.EDIT);
        } else {
            return new Breadcrumbs().add(new InternalAccountsBreadcrumbsElement())
                    .add(new InternalAccountBreadcrumbsElement(getAccount()))
                    .add(ClobParamBreadcrumbsElement.createTermsBreadcrumbsElement(accountId))
                    .add(ActionBreadcrumbs.EDIT);
        }
    }
}
