package app.programmatic.ui.agentreport.restriction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.common.permission.service.PermissionService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;

import static app.programmatic.ui.common.permission.dao.model.PermissionAction.EDIT;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.VIEW;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.AGENT_REPORT;

@Service
@Restrictions
public class AgentReportRestrictions {

    @Autowired
    private PermissionService permissionService;

    @Restriction("agentReport.view")
    public boolean canView() {
        return permissionService.isGranted(AGENT_REPORT, VIEW);
    }

    @Restriction("agentReport.edit")
    public boolean canEdit() {
        return permissionService.isGranted(AGENT_REPORT, EDIT);
    }
}
