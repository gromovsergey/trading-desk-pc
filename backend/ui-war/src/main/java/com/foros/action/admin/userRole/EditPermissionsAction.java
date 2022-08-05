package com.foros.action.admin.userRole;

import com.foros.action.admin.permissions.PolicyTable;
import com.foros.action.admin.permissions.PolicyTableFactory;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.security.UserRole;
import com.foros.restriction.annotation.Restrict;
import com.foros.restriction.permission.PermissionService;
import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.util.permissions.PermissionsZone;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;

public class EditPermissionsAction extends PermissionsActionSupport implements BreadcrumbsSupport {

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

    @EJB
    private PermissionService permissionService;

    @EJB
    private UserRoleService userRoleService;

    private Map<String, Map<String, PermissionDescriptor>> toMap(Set<PermissionDescriptor> descriptorSet) {
        HashMap<String, Map<String, PermissionDescriptor>> map = new HashMap<String, Map<String, PermissionDescriptor>>();

        for (PermissionDescriptor descriptor : descriptorSet) {
            Map<String, PermissionDescriptor> descriptorMap = map.get(descriptor.getObjectType());
            if (descriptorMap == null) {
                descriptorMap = new HashMap<String, PermissionDescriptor>();
                map.put(descriptor.getObjectType(), descriptorMap);
            }
            descriptorMap.put(descriptor.getActionName(), descriptor);
        }

        return map;
    }

    private void preparePermissions(UserRole role) {
        Map<String, Map<String, PermissionDescriptor>> permissions =
                toMap(userRoleService.getAvailablePermissions(role));

        Map<PermissionDescriptor, Map<String, Long>> policy = permissionService.getPolicy(role.getId());

        PolicyTableFactory tableFactory = new PolicyTableFactory(policy, permissions);

        this.defaultPolicy = tableFactory.getPolicyTable(PermissionsZone.DEFAULT);
        this.predefinedReportsPolicy = tableFactory.getPolicyTable(PermissionsZone.PREDEFINED_REPORTS);
        this.birtReportsPolicy = tableFactory.getPolicyTable(PermissionsZone.BIRT_REPORTS);
        this.agentReportPolicy = tableFactory.getPolicyTable(PermissionsZone.AGENT_REPORT);
        this.audienceResearchPolicy = tableFactory.getPolicyTable(PermissionsZone.AUDIENCE_RESEARCH);

        this.advertiserPolicy = tableFactory.getPolicyTable(PermissionsZone.ADVERTISER);
        this.publisherPolicy = tableFactory.getPolicyTable(PermissionsZone.PUBLISHER);
        this.ispPolicy = tableFactory.getPolicyTable(PermissionsZone.ISP);
        this.cmpPolicy = tableFactory.getPolicyTable(PermissionsZone.CMP);
        this.internalPolicy = tableFactory.getPolicyTable(PermissionsZone.INTERNAL);
        this.adminPolicy = tableFactory.getPolicyTable(PermissionsZone.ADMIN);
        this.apiPolicy = tableFactory.getPolicyTable(PermissionsZone.API);
    }

    @ReadOnly
    @Restrict(restriction="UserRole.update")
    public String edit() {
        this.entity = userRoleService.view(getUserRole().getId());
        preparePermissions(entity);
        return SUCCESS;
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

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new UserRolesBreadcrumbsElement()).add(new UserRoleBreadcrumbsElement(entity)).add(new PermissionsBreadcrumbsElement());
    }
}
