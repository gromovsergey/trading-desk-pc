package app.programmatic.ui.common.permission.service;

import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.common.permission.dao.PermissionRepository;
import app.programmatic.ui.common.permission.dao.model.Permission;
import app.programmatic.ui.common.permission.dao.model.PermissionAction;
import app.programmatic.ui.common.permission.dao.model.PermissionType;
import app.programmatic.ui.user.dao.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public boolean isGranted(PermissionType permissionType, PermissionAction action) {
        return isGranted(permissionType, action, null);
    }

    @Override
    public boolean isGranted(PermissionType permissionType, PermissionAction action, String parameter) {
        User user = authorizationService.getAuthUser();
        List<Permission> permissions = permissionRepository.findByUserRoleIdAndTypeAndActionAndParameter(
                user.getUserRole().getId(),
                permissionType.getStoredValue(),
                action.getStoredValue(),
                parameter
        );

        if (permissions.size() > 1) {
            throw new IllegalStateException("Policy table has several entries for key (" +
                    "object type = " + permissionType.getStoredValue() +
                    ", action type = " + action +
                    ", parameter = " + parameter
            );
        }

        return !permissions.isEmpty();
    }
}
