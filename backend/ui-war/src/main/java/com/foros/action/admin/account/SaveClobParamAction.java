package com.foros.action.admin.account;

import com.foros.action.account.InternalAccountBreadcrumbsElement;
import com.foros.action.account.InternalAccountsBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.ClobParamPK;
import com.foros.model.ClobParamType;

public class SaveClobParamAction extends ClobParamActionSupport implements BreadcrumbsSupport {

    public String save() {
        param.setId(new ClobParamPK(accountId, type));
        clobParamService.update(param);
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
