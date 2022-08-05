package app.programmatic.ui.common.permission.service;

import app.programmatic.ui.common.permission.dao.model.PermissionAction;
import app.programmatic.ui.common.permission.dao.model.PermissionType;

public interface PermissionService {
    boolean isGranted(PermissionType permissionType, PermissionAction action);

    boolean isGranted(PermissionType permissionType, PermissionAction action, String parameter);
}
