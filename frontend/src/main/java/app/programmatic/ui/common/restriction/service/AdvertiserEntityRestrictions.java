package app.programmatic.ui.common.restriction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.permission.service.PermissionService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;

import static app.programmatic.ui.common.permission.dao.model.PermissionAction.CREATE;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.EDIT;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.VIEW;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.ADVERTISER_ENTITY;

@Service
@Restrictions
public class AdvertiserEntityRestrictions {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private EntityRestrictions entityRestrictions;

    @Autowired
    private AccountService accountService;

    @Restriction("advertiserEntity.create")
    public boolean canCreate(Long accountId) {
        if (!permissionService.isGranted(ADVERTISER_ENTITY, CREATE)) {
            return false;
        }

        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("advertiserEntity.update")
    public boolean canUpdate(Long accountId) {
        if (!permissionService.isGranted(ADVERTISER_ENTITY, EDIT)) {
            return false;
        }

        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("advertiserEntity.view")
    public boolean canView() {
        return permissionService.isGranted(ADVERTISER_ENTITY, VIEW);
    }
}
