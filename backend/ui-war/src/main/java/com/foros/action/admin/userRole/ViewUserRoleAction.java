package com.foros.action.admin.userRole;

import com.foros.action.admin.permissions.Parameter;
import com.foros.action.admin.permissions.PolicyTable;
import com.foros.action.admin.permissions.PolicyTableFactory;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.security.UserRole;
import com.foros.restriction.annotation.Restrict;
import com.foros.restriction.permission.PermissionService;
import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.restriction.registry.PermissionRegistryService;
import com.foros.session.EntityTO;
import com.foros.session.NamedTO;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.NamedTOConverter;
import com.foros.util.permissions.PermissionsZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

public class ViewUserRoleAction extends UserRoleActionSupport implements BreadcrumbsSupport {

    @EJB
    private PermissionService permissionService;

    @EJB
    private PermissionRegistryService permissionRegistryService;

    private PolicyTable defaultPolicy;
    private PolicyTable predefinedReportsPolicy;
    private PolicyTable birtReportsPolicy;
    private PolicyTable agentReportPolicy;
    private PolicyTable audienceResearchPolicy;

    private PolicyTable advertiserPolicy;
    private PolicyTable publisherPolicy;
    private PolicyTable ispPolicy;
    private PolicyTable cmpPolicy;
    private PolicyTable internalPolicy;
    private PolicyTable adminPolicy;
    private PolicyTable apiPolicy;

    private boolean permissionsSet;

    private List<NamedTO> accessAccounts;

    @ReadOnly
    @Restrict(restriction="UserRole.view")
    public String view() {
        entity = service.findById(getModel().getId());
        preparePermissions();
        return SUCCESS;
    }

    private void preparePermissions() {
        UserRole role = getModel();
        Map<String, Map<String, PermissionDescriptor>> permissions =
                permissionRegistryService.getPermissions(role.getAccountRole());

        Map<PermissionDescriptor, Map<String, Long>> policy = permissionService.getPolicy(getModel().getId());

        PolicyTableFactory tableFactory = new PolicyTableFactory(policy, permissions);

        defaultPolicy = tableFactory.getPolicyTable(PermissionsZone.DEFAULT);
        predefinedReportsPolicy = tableFactory.getPolicyTable(PermissionsZone.PREDEFINED_REPORTS);
        birtReportsPolicy = tableFactory.getPolicyTable(PermissionsZone.BIRT_REPORTS);
        agentReportPolicy = tableFactory.getPolicyTable(PermissionsZone.AGENT_REPORT);
        audienceResearchPolicy = tableFactory.getPolicyTable(PermissionsZone.AUDIENCE_RESEARCH);

        this.advertiserPolicy = tableFactory.getPolicyTable(PermissionsZone.ADVERTISER);
        this.publisherPolicy = tableFactory.getPolicyTable(PermissionsZone.PUBLISHER);
        this.ispPolicy = tableFactory.getPolicyTable(PermissionsZone.ISP);
        this.cmpPolicy = tableFactory.getPolicyTable(PermissionsZone.CMP);
        this.internalPolicy = tableFactory.getPolicyTable(PermissionsZone.INTERNAL);
        this.adminPolicy = tableFactory.getPolicyTable(PermissionsZone.ADMIN);
        this.apiPolicy = tableFactory.getPolicyTable(PermissionsZone.API);

        permissionsSet = defaultPolicy.hasPolicy() || predefinedReportsPolicy.hasPolicy() || birtReportsPolicy.hasPolicy() ||
                agentReportPolicy.hasPolicy() || audienceResearchPolicy.hasPolicy() || advertiserPolicy.hasPolicy() || publisherPolicy.hasPolicy() ||
                ispPolicy.hasPolicy() || cmpPolicy.hasPolicy() || internalPolicy.hasPolicy() || adminPolicy.hasPolicy() || apiPolicy.hasPolicy();
    }

    public boolean isPermissionsSet() {
        return permissionsSet;
    }

    public String getAccountManagers() {
        StringBuilder sbAccountManager = new StringBuilder();
        boolean isSeparatorRequired = false;
        if (getModel().isAdvertiserAccountManager()) {
            sbAccountManager.append(getText("UserRole.advertiser"));
            isSeparatorRequired = true;
        }
        if (getModel().isPublisherAccountManager()) {
            if (isSeparatorRequired) {
                sbAccountManager.append(", ");
            }
            sbAccountManager.append(getText("UserRole.publisher"));
            isSeparatorRequired = true;
        }
        if (getModel().isISPAccountManager()) {
            if (isSeparatorRequired) {
                sbAccountManager.append(", ");
            }
            sbAccountManager.append(getText("UserRole.isp"));
            isSeparatorRequired = true;
        }
        if (getModel().isCMPAccountManager()) {
            if (isSeparatorRequired) {
                sbAccountManager.append(", ");
            }
            sbAccountManager.append(getText("UserRole.cmp"));
        }

        return sbAccountManager.toString();
    }

    public PolicyTable getDefaultPolicy() {
        return defaultPolicy;
    }

    public PolicyTable getPredefinedReportsPolicy() {
        return predefinedReportsPolicy;
    }

    public PolicyTable getBirtReportsPolicy() {
        return birtReportsPolicy;
    }

    public PolicyTable getAgentReportPolicy() {
        return agentReportPolicy;
    }

    public PolicyTable getAudienceResearchPolicy() {
        return audienceResearchPolicy;
    }

    public PolicyTable getAdvertiserPolicy() {
        return advertiserPolicy;
    }

    public PolicyTable getPublisherPolicy() {
        return publisherPolicy;
    }

    public PolicyTable getIspPolicy() {
        return ispPolicy;
    }

    public PolicyTable getCmpPolicy() {
        return cmpPolicy;
    }

    public PolicyTable getInternalPolicy() {
        return internalPolicy;
    }

    public PolicyTable getAdminPolicy() {
        return adminPolicy;
    }

    public PolicyTable getApiPolicy() {
        return apiPolicy;
    }

    public Parameter getNullParameter() {
        return Parameter.nullParameter();
    }

    public List<NamedTO> getAccessAccounts(){
        if (accessAccounts == null) {
            List<EntityTO> list = accountService.getInternalAccountsWithoutRestricted(true);
            list.retainAll(CollectionUtils.convert(new NamedTOConverter(), getModel().getAccessAccountIds()));
            accessAccounts = new ArrayList<NamedTO>();
            accessAccounts.add(new NamedTO(null, getText("UserRole.InternalAccessType.view.USER_ACCOUNT")));
            accessAccounts.addAll(list);
            
        }
        return accessAccounts;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new UserRolesBreadcrumbsElement()).add(new UserRoleBreadcrumbsElement(entity));
    }
}
