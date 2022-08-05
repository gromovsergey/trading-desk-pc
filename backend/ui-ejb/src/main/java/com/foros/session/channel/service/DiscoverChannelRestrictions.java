package com.foros.session.channel.service;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.security.NotManagedEntity;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.CurrentUserService;
import com.foros.session.GenericEntityService;
import com.foros.session.admin.categoryChannel.CategoryChannelRestrictions;
import com.foros.session.bulk.OperationType;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "discoverChannel", action = "view", accountRoles = {INTERNAL}),
        @Permission(objectType = "discoverChannel", action = "create", accountRoles = {INTERNAL}),
        @Permission(objectType = "discoverChannel", action = "edit", accountRoles = {INTERNAL}),
        @Permission(objectType = "discoverChannel", action = "undelete", accountRoles = {INTERNAL})
})
public class DiscoverChannelRestrictions {

    private static final boolean IS_MANAGED = NotManagedEntity.Util.isManaged(DiscoverChannel.class, DiscoverChannelList.class);

    @EJB
    private PermissionService permissionService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private GenericEntityService genericEntityService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private CategoryChannelRestrictions categoryChannelRestrictions;
    
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Restriction
    public boolean canList() {
        return currentUserService.isInternal();
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("discoverChannel", "view");
    }

    @Restriction
    public boolean canView(DiscoverChannel entity) {
        return canViewInternal(entity);
    }

    @Restriction
    public boolean canView(DiscoverChannelList entity) {
        return canViewInternal(entity);
    }

    private boolean canViewInternal(Channel entity) {
        return canView() && entityRestrictions.canView(entity);
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("discoverChannel", "create");
    }

    @Restriction
    public boolean canCreate(Account account) {
        return permissionService.isGranted("discoverChannel", "create") && entityRestrictions.canCreate(account, IS_MANAGED);
    }

    @Restriction
    public boolean canCreateCopy(DiscoverChannel entity) {
        return !isChannelInList(entity)
                && canUpdate()
                && canCreate()
                && entityRestrictions.canCreateCopy(entity, entity.getAccount());
    }

    private boolean isChannelInList(DiscoverChannel entity) {
        return entity.getChannelList() != null;
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("discoverChannel", "edit");
    }

    @Restriction
    public boolean canUpdate(DiscoverChannel entity) {
        return canUpdateInternal(entity);
    }

    @Restriction
    public boolean canUpdate(DiscoverChannelList entity) {
        return canUpdateInternal(entity);
    }

    private boolean canUpdateInternal(Channel entity) {
        return permissionService.isGranted("discoverChannel", "edit") && entityRestrictions.canUpdate(entity);
    }

    @Restriction
    public boolean canInactivate(DiscoverChannel entity) {
        return canInactivateInternal(entity) && isNotLinkedToInactiveList(entity);
    }

    @Restriction
    public boolean canInactivate(DiscoverChannelList entity) {
        return canInactivateInternal(entity);
    }

    private boolean canInactivateInternal(Channel entity) {
        return permissionService.isGranted("discoverChannel", "edit") && entityRestrictions.canInactivate(entity);
    }

    @Restriction
    public boolean canActivate(DiscoverChannel entity) {
        return canActivateInternal(entity) && isNotLinkedToInactiveList(entity);
    }

    private boolean isNotLinkedToInactiveList(DiscoverChannel entity) {
        return (entity.getChannelList() == null || entity.getChannelList().getStatus() != Status.INACTIVE);
    }

    @Restriction
    public boolean canActivate(DiscoverChannelList entity) {
        return canActivateInternal(entity);
    }

    private boolean canActivateInternal(Channel entity) {
        return permissionService.isGranted("discoverChannel", "edit") && entityRestrictions.canActivate(entity);
    }

    @Restriction
    public boolean canDelete(DiscoverChannel entity) {
        return canDeleteInternal(entity);
    }

    @Restriction
    public boolean canDelete(DiscoverChannelList entity) {
        return canDeleteInternal(entity);
    }

    private boolean canDeleteInternal(Channel entity) {
        return permissionService.isGranted("discoverChannel", "edit") && entityRestrictions.canDelete(entity);
    }

    @Restriction
    public boolean canUndelete(DiscoverChannel entity) {
        return canUndeleteInternal(entity);
    }

    @Restriction
    public boolean canUndelete(DiscoverChannelList entity) {
        return canUndeleteInternal(entity);
    }

    private boolean canUndeleteInternal(Channel entity) {
        return permissionService.isGranted("discoverChannel", "undelete") && entityRestrictions.canUndelete(entity);
    }

    @Restriction
    public boolean canUnlink(DiscoverChannel entity) {
        return permissionService.isGranted("discoverChannel", "edit") && !genericEntityService.isDeleted(entity);
    }

    @Restriction
    public boolean canLink(DiscoverChannel entity) {
        return permissionService.isGranted("discoverChannel", "edit") && !genericEntityService.isDeleted(entity);
    }

    @Restriction
    public boolean canEditCategories(DiscoverChannel channel) {
        return canEditCategoriesInternal(channel) && !isChannelInList(channel);
    }

    @Restriction
    public boolean canEditCategories(DiscoverChannelList channel) {
        return canEditCategoriesInternal(channel);
    }

    private boolean canEditCategoriesInternal(Channel channel) {
        return canUpdateInternal(channel) && categoryChannelRestrictions.canView();
    }

    @Restriction
    public boolean canMerge(DiscoverChannel channel, OperationType operationType) {
        boolean isPermit;
        switch (operationType) {
            case CREATE:
                isPermit = canCreate(find(Account.class, channel.getAccount().getId()));
                break;
            case UPDATE:
                DiscoverChannel existing = find(DiscoverChannel.class, channel.getId());
                isPermit = permissionService.isGranted("discoverChannel", "edit")
                && (entityRestrictions.canUpdate(existing) || (isUndeleted(channel, existing) && canUndelete(existing)));
                break;
            default:
                throw new RuntimeException(operationType + " does not supported!");
        }
        return isPermit;        
    }
    
    private boolean isUndeleted(DiscoverChannel entity, DiscoverChannel existing) {
        return existing.getStatus() == Status.DELETED && entity.getStatus() != Status.DELETED;
    }
    
    private <T extends Identifiable> T find(Class<T> entityClass, Long id) {
        if (id == null) {
            throw new EntityNotFoundException("Entity with id == null not found");
        }
        T t = em.find(entityClass, id);
        if (t == null) {
            throw new EntityNotFoundException("Entity with id " + id + " not found"); 
        }
        return t;
    }

    @Restriction
    public boolean canApprove(DiscoverChannel entity) {
        return false;
    }

    @Restriction
    public boolean canDecline(DiscoverChannel entity) {
        return false;
    }
}
