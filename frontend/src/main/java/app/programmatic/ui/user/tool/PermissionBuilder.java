package app.programmatic.ui.user.tool;

import app.programmatic.ui.common.permission.dao.model.Permission;
import app.programmatic.ui.common.permission.dao.model.PermissionAction;
import app.programmatic.ui.common.permission.dao.model.PermissionType;
import app.programmatic.ui.user.dao.model.UserRoleType;

import java.util.ArrayList;
import java.util.HashSet;

import static app.programmatic.ui.common.permission.dao.model.PermissionAction.CREATE;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.EDIT;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.RUN;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.VIEW;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.VIEW_SYSTEM_FINANCE;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.*;


public class PermissionBuilder {

    public static ArrayList<Permission> buildFor(Long roleId, UserRoleType roleType) {
        switch (roleType) {
            case ADMIN: return buildForAdmin(roleId);
            case STANDARD: return buildForStandard(roleId);
            case READ_ONLY: return buildForReadOnly(roleId);
            case READ_ONLY_PLUS: return buildForReadOnlyPlus(roleId);
            case ADV_ADMIN: return buildForAdvAdmin(roleId);
            case ADV_STANDART: return buildForAdvStandard(roleId);
            case ADV_READ_ONLY: return buildForAdvReadOnly(roleId);
            default: throw new IllegalArgumentException("Unexpected user role type: " + roleType);
        }
    }

    public static ArrayList<Permission> buildForReadOnly(Long roleId) {
        ArrayList<Permission> result = new ArrayList<>();

        result.add(createPermission(roleId, ADVERTISER_ADVERTISING_CHANNEL, VIEW));
        result.add(createPermission(roleId, ADVERTISER_ENTITY, VIEW));
        result.add(createPermission(roleId, ADVERTISING_ACCOUNT, VIEW));
        result.add(createPermission(roleId, AGENCY_ADVERTISER_ACCOUNT, VIEW));
        result.add(createPermission(roleId, API, RUN));
        result.add(createPermission(roleId, REPORT_GENERAL_ADVERTISING, RUN));
        result.add(createPermission(roleId, REPORT_CONVERSIONS, RUN));

        return result;
    }

    public static ArrayList<Permission> buildForReadOnlyPlus(Long roleId) {
        ArrayList<Permission> result = buildForReadOnly(roleId);
        result.add(createPermission(roleId, ADVERTISING_ACCOUNT, VIEW_SYSTEM_FINANCE));
        return result;
    }

    public static ArrayList<Permission> buildForStandard(Long roleId) {
        ArrayList<Permission> result = buildForReadOnlyPlus(roleId);

        result.add(createPermission(roleId, ADVERTISER_ADVERTISING_CHANNEL, CREATE));
        result.add(createPermission(roleId, ADVERTISER_ADVERTISING_CHANNEL, EDIT));
        result.add(createPermission(roleId, ADVERTISER_ENTITY, CREATE));
        result.add(createPermission(roleId, ADVERTISER_ENTITY, EDIT));
        result.add(createPermission(roleId, AGENCY_ADVERTISER_ACCOUNT, CREATE));
        result.add(createPermission(roleId, AGENCY_ADVERTISER_ACCOUNT, EDIT));

        return result;
    }

    public static ArrayList<Permission> buildForAdmin(Long roleId) {
        ArrayList<Permission> result = buildForStandard(roleId);

        result.add(createPermission(roleId, ADVERTISING_ACCOUNT, EDIT));

        return result;
    }

    public static ArrayList<Permission> buildForAdvReadOnly(Long roleId) {
        ArrayList<Permission> result = new ArrayList<>();

        result.add(createPermission(roleId, ADVERTISER_ADVERTISING_CHANNEL, VIEW));
        result.add(createPermission(roleId, ADVERTISER_ENTITY, VIEW));
        result.add(createPermission(roleId, ADVERTISING_ACCOUNT, VIEW));
        result.add(createPermission(roleId, API, RUN));
        result.add(createPermission(roleId, REPORT_GENERAL_ADVERTISING, RUN));
        result.add(createPermission(roleId, REPORT_CONVERSIONS, RUN));

        return result;
    }

    public static ArrayList<Permission> buildForAdvStandard(Long roleId) {
        ArrayList<Permission> result = buildForReadOnly(roleId);

        result.add(createPermission(roleId, ADVERTISER_ADVERTISING_CHANNEL, CREATE));
        result.add(createPermission(roleId, ADVERTISER_ADVERTISING_CHANNEL, EDIT));
        result.add(createPermission(roleId, ADVERTISER_ENTITY, CREATE));
        result.add(createPermission(roleId, ADVERTISER_ENTITY, EDIT));

        return result;
    }

    public static ArrayList<Permission> buildForAdvAdmin(Long roleId) {
        ArrayList<Permission> result = buildForStandard(roleId);

        result.add(createPermission(roleId, ADVERTISING_ACCOUNT, EDIT));

        return result;
    }

    public static HashSet<Permission> iterableToSet(Iterable<Permission> src) {
        HashSet<Permission> result = new HashSet<>();
        src.forEach( permission -> result.add(permission) );
        return result;
    }

    private static Permission createPermission(Long roleId, PermissionType type, PermissionAction action) {
        Permission result = new Permission();

        result.setUserRoleId(roleId);
        result.setType(type.getStoredValue());
        result.setAction(action.getStoredValue());

        return result;
    }
}
