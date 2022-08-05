package app.programmatic.ui.user.service;

import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.changetrack.service.ChangeTrackerService;
import app.programmatic.ui.changetrack.dao.model.TableName;
import app.programmatic.ui.common.permission.dao.PermissionRepository;
import app.programmatic.ui.common.permission.dao.model.Permission;
import app.programmatic.ui.common.permission.dao.model.PermissionAction;
import app.programmatic.ui.common.permission.dao.model.PermissionType;
import app.programmatic.ui.user.dao.UserRoleRepository;
import app.programmatic.ui.user.dao.model.*;
import app.programmatic.ui.user.tool.PermissionBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import javax.annotation.PostConstruct;

import static app.programmatic.ui.user.dao.model.UserRoleOpts.AUTO_GENERATED;
import static app.programmatic.ui.user.dao.model.UserRoleType.*;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private ChangeTrackerService changeTrackerService;

    private Long adminUserRoleId;
    private Long standardUserRoleId;
    private Long readOnlyUserRoleId;
    private Long readOnlyPlusUserRoleId;
    private Long advAdminUserRoleId;
    private Long advStandardUserRoleId;
    private Long advReadOnlyUserRoleId;

    @Override
    public UserRole findForCreate(Long id) {
        if (!existsForCreate(id)) {
            return null;
        }
        return userRoleRepository.findById(id).orElse(null);
    }

    @Override
    public boolean existsForCreate(Long id) {
        return adminUserRoleId.equals(id) || standardUserRoleId.equals(id) || readOnlyUserRoleId.equals(id) || readOnlyPlusUserRoleId.equals(id) ||
                advAdminUserRoleId.equals(id) || advStandardUserRoleId.equals(id) || advReadOnlyUserRoleId.equals(id);
    }

    @Override
    public Iterable<UserRole> getAvailableForCreateRoles(AccountRole accountRole) {
        switch (accountRole) {
            case AGENCY:
                return userRoleRepository.findAllById(Arrays.asList(adminUserRoleId, standardUserRoleId, readOnlyUserRoleId, readOnlyPlusUserRoleId));
            case ADVERTISER:
                return userRoleRepository.findAllById(Arrays.asList(advAdminUserRoleId, advStandardUserRoleId, advReadOnlyUserRoleId));
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean hasPermission(Long userRoleId, PermissionType type, PermissionAction action) {
        return permissionRepository.findByUserRoleId(userRoleId).stream()
                .filter(p -> PermissionType.findByStoredValue(p.getType()) == type && PermissionAction.findByStoredValue(p.getAction()) == action)
                .count() > 0;
    }

    @PostConstruct
    public void init() {
        adminUserRoleId = findOrCreateUserRoleId(ADMIN);
        standardUserRoleId = findOrCreateUserRoleId(STANDARD);
        readOnlyUserRoleId = findOrCreateUserRoleId(READ_ONLY);
        readOnlyPlusUserRoleId = findOrCreateUserRoleId(READ_ONLY_PLUS);
        advAdminUserRoleId = findOrCreateUserRoleId(ADV_ADMIN);
        advStandardUserRoleId = findOrCreateUserRoleId(ADV_STANDART);
        advReadOnlyUserRoleId = findOrCreateUserRoleId(ADV_READ_ONLY);
    }

    private Long findOrCreateUserRoleId(UserRoleType roleType) {
        Long result = findOrCreateUserRole(roleType);

        HashSet<Permission> required = new HashSet<>(PermissionBuilder.buildFor(result, roleType));
        HashSet<Permission> stored = PermissionBuilder.iterableToSet(permissionRepository.findByUserRoleId(result));

        HashSet<Permission> excessive = new HashSet<>(stored);
        excessive.removeAll(required);

        HashSet<Permission> missing = new HashSet<>(required);
        missing.removeAll(stored);

        permissionRepository.deleteAll(excessive);
        permissionRepository.saveAll(missing);

        return result;
    }

    private Long findOrCreateUserRole(UserRoleType roleType) {
        Long result = findUserRoleId(roleType.getName());
        if (result != null) {
            return result;
        }

        UserRole role = new UserRole();
        role.setAccountRole(roleType.getAccountRole());
        role.setName(roleType.getName());
        role.setFlagsSet(EnumSet.of(AUTO_GENERATED));
        role.setLdapDn("");

        result = userRoleRepository.save(role).getId();

        changeTrackerService.saveChange(TableName.USER_ROLE, result);

        return result;
    }

    private Long findUserRoleId(String name) {
        try {
            return jdbcOperations.queryForObject(
                    "select user_role_id from userrole " +
                    "  where name = '" + name + "' " +
                    "  and (flags::int & x'" + Long.toHexString(1 << AUTO_GENERATED.ordinal()) + "'::int)::bool",
                    Long.class);
        } catch (Exception e) {
            return null;
        }
    }
}
