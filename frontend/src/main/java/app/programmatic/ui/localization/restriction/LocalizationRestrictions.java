package app.programmatic.ui.localization.restriction;

import static app.programmatic.ui.account.dao.model.AccountRole.INTERNAL;

import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Restrictions
public class LocalizationRestrictions {

    @Autowired
    private AuthorizationService authorizationService;

    @Restriction("localization.update")
    public boolean canUpdate() {
        return INTERNAL == authorizationService.getAuthUser().getUserRole().getAccountRole();
    }
}
