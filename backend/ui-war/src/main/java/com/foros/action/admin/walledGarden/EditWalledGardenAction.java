package com.foros.action.admin.walledGarden;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.admin.WalledGarden;

public class EditWalledGardenAction extends EditSaveWalledGardenActionBase implements BreadcrumbsSupport {
    private Long id;

    private Breadcrumbs breadcrumbs = new Breadcrumbs().add(new WalledGardenBreadcrumbsElement());

    @ReadOnly
    public String create() {
        entity = new WalledGarden();
        breadcrumbs.add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        entity = walledGardenService.view(id);
        breadcrumbs.add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
