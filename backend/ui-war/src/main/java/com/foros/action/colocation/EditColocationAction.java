package com.foros.action.colocation;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.util.EntityUtils;

import javax.persistence.EntityNotFoundException;

public class EditColocationAction extends ColocationActionSupport implements BreadcrumbsSupport {

    // parameter
    private Long accountId;

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    public String create() {
        colocation = createEmptyColocation(accountId);
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        if (colocation.getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }
        colocation = EntityUtils.applyOwnerStatusRule(colocationService.find(colocation.getId()));
        breadcrumbs = new Breadcrumbs().add(new ColocationBreadcrumbsElement(colocation)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    private Colocation createEmptyColocation(Long accountId) {
        Colocation result = new Colocation();
        result.setAccount(new IspAccount(accountId));
        return result;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
