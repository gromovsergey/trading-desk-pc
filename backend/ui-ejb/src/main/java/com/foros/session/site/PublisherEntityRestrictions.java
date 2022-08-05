package com.foros.session.site;

import static com.foros.security.AccountRole.INTERNAL;
import static com.foros.security.AccountRole.PUBLISHER;

import com.foros.model.OwnedApprovable;
import com.foros.model.account.Account;
import com.foros.model.security.NotManagedEntity;
import com.foros.model.security.OwnedStatusable;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.WDTag;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountService;
import com.foros.session.restriction.EntityRestrictions;
import com.foros.session.template.TemplateService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@LocalBean
@Stateless
@Restrictions
@Permissions({
    @Permission(objectType = "publisher_entity", action = "view", accountRoles = {INTERNAL, PUBLISHER}),
    @Permission(objectType = "publisher_entity", action = "create", accountRoles = {INTERNAL, PUBLISHER}),
    @Permission(objectType = "publisher_entity", action = "edit", accountRoles = {INTERNAL, PUBLISHER}),
    @Permission(objectType = "publisher_entity", action = "undelete", accountRoles = {INTERNAL}),
    @Permission(objectType = "publisher_entity", action = "approve", accountRoles = {INTERNAL}),
    @Permission(objectType = "publisher_entity", action = "advanced", accountRoles = {INTERNAL})
})
public class PublisherEntityRestrictions {

    @EJB
    private AccountService accountService;

    private static final boolean IS_MANAGED = NotManagedEntity.Util.isManaged(Site.class, Tag.class, WDTag.class);

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private TemplateService templateService;

    @EJB
    private SiteService siteService;

    @EJB
    private CurrentUserService currentUserService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("publisher_entity", "view");
    }

    @Restriction
    public boolean canView(OwnedStatusable ownedStatusable) {
        return canView() && entityRestrictions.canView(ownedStatusable);
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("publisher_entity", "create");
    }

    @Restriction
    public boolean canCreate(OwnedStatusable ownedStatusable) {
        return canCreate() && entityRestrictions.canCreate(ownedStatusable, IS_MANAGED);
    }

    @Restriction
    public boolean canCreateOrUpdate(OwnedStatusable ownedStatusable) {
        return canCreate(ownedStatusable) || canUpdate(ownedStatusable);
    }

    @Restriction
    public boolean canCreateWDTag(OwnedStatusable ownedStatusable) {
        return canCreate(ownedStatusable) && ownedStatusable.getAccount().getAccountType().isWdTagsFlag();
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("publisher_entity", "edit");
    }

    @Restriction
    public boolean canUpdate(OwnedStatusable ownedStatusable) {
        return canUpdate() && entityRestrictions.canUpdate(ownedStatusable);
    }

    @Restriction
    public boolean canUpload(Long publisherId) {
        if (publisherId != null) {
            Account account = accountService.find(publisherId);
            return canCreate(account) && entityRestrictions.canUpdate(account);
        }
        return currentUserService.isInternal() && canUpdate();
    }

    @Restriction
    public boolean canDelete(OwnedStatusable ownedStatusable) {
        return canUpdate(ownedStatusable) && entityRestrictions.canDelete(ownedStatusable);
    }

    @Restriction
    public boolean canUndelete(OwnedStatusable ownedStatusable) {
        return permissionService.isGranted("publisher_entity", "undelete") && entityRestrictions.canUndelete(ownedStatusable);
    }

    @Restriction
    public boolean canApprove() {
        return permissionService.isGranted("publisher_entity", "approve");
    }

    @Restriction
    public boolean canApprove(OwnedApprovable ownedApprovable) {
        return permissionService.isGranted("publisher_entity", "approve") && entityRestrictions.canApprove(ownedApprovable);
    }

    @Restriction
    public boolean canDecline(OwnedApprovable ownedApprovable) {
        return permissionService.isGranted("publisher_entity", "approve") && entityRestrictions.canDecline(ownedApprovable);
    }

    @Restriction
    public boolean canReviewCreatives(Long siteId){
        Site site = siteService.find(siteId);
        return canUpdate(site) && site.getAccount().getAccountType().isAdvExclusionFlag()
                && site.getAccount().getAccountType().isAdvExclusionApprovalAllowed();
    }

    @Restriction
    public boolean canViewCreativesApproval(OwnedStatusable ownedStatusable){
        return canView(ownedStatusable) && ownedStatusable.getAccount().getAccountType().isAdvExclusionFlag();
    }

    @Restriction
    public boolean canUpdateOptions(Tag tag) {
        return canUpdate(tag);
    }

    @Restriction
    public boolean canAdvanced() {
        return permissionService.isGranted("publisher_entity", "advanced");
    }
}
