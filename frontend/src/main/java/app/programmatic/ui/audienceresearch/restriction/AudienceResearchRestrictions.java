package app.programmatic.ui.audienceresearch.restriction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.common.permission.service.PermissionService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;

import static app.programmatic.ui.common.permission.dao.model.PermissionAction.EDIT;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.VIEW;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.AUDIENCE_RESEARCH;

@Service
@Restrictions
public class AudienceResearchRestrictions {

    @Autowired
    private PermissionService permissionService;

    @Restriction("audienceResearch.view")
    public boolean canView() {
        return permissionService.isGranted(AUDIENCE_RESEARCH, VIEW);
    }

    @Restriction("audienceResearch.edit")
    public boolean canEdit() {
        return permissionService.isGranted(AUDIENCE_RESEARCH, EDIT);
    }
}
