package app.programmatic.ui.user.service;

import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.common.permission.dao.model.PermissionAction;
import app.programmatic.ui.common.permission.dao.model.PermissionType;
import app.programmatic.ui.user.dao.model.UserRole;

public interface UserRoleService {

    UserRole findForCreate(Long id);

    boolean existsForCreate(Long id);

    Iterable<UserRole> getAvailableForCreateRoles(AccountRole accountRole);

    boolean hasPermission(Long userRoleId, PermissionType type, PermissionAction action);
}
