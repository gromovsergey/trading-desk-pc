package com.foros.session.template;

import static com.foros.security.AccountRole.INTERNAL;
import com.foros.model.template.Template;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.GenericEntityService;
import com.foros.session.StatusAction;
import com.foros.session.UtilityService;
import com.foros.session.status.StatusService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
    @Permission(objectType = "template", action = "create", accountRoles = {INTERNAL}),
    @Permission(objectType = "template", action = "edit", accountRoles = {INTERNAL}),
    @Permission(objectType = "template", action = "view", accountRoles = {INTERNAL}),
    @Permission(objectType = "template", action = "undelete", accountRoles = {INTERNAL})
})
public class TemplateRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private StatusService statusService;

    @EJB
    private GenericEntityService genericEntityService;

    @EJB
    private UtilityService utilityService;

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("template", "create");
    }

    @Restriction
    public boolean canCreateCopy(Template entity) {
        return canCreate() && canUpdateInternal(entity);
    }

    @Restriction
    public boolean canUpdate(Template entity) {
        return canUpdateInternal(entity) && isNotTextTemplate(entity);
    }

    @Restriction
    public boolean canUpdateOptions(Template entity) {
        return canUpdateInternal(entity);
    }

    @Restriction
    public boolean canUpdateFilesMatch(Template entity) {
        return canUpdateInternal(entity);
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("template", "view");
    }
    
    @Restriction
    public boolean canViewFileManager() {
        return permissionService.isGranted("template", "edit") || canCreate();
    }

    @Restriction
    public boolean canDelete(Template entity) {
        return permissionService.isGranted("template", "edit") &&
                statusService.isActionAvailable(entity, StatusAction.DELETE) &&
                isNotTextTemplate(entity);
    }

    @Restriction
    public boolean canDeleteOptions(Template entity) {
        return canDelete(entity);
    }

    @Restriction
    public boolean canUndelete(Template entity) {
        return permissionService.isGranted("template", "undelete") &&
                statusService.isActionAvailable(entity, StatusAction.UNDELETE) &&
                isNotTextTemplate(entity);
    }

    private boolean canUpdateInternal(Template entity) {
        return permissionService.isGranted("template", "edit") && !genericEntityService.isDeleted(entity);
    }

    private boolean isNotTextTemplate(Template entity) {
        Template template = utilityService.find(Template.class, entity.getId());
        return !Template.TEXT_TEMPLATE.equals(template.getDefaultName());
    }
}
