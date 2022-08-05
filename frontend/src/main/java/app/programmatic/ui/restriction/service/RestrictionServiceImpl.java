package app.programmatic.ui.restriction.service;

import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.error.ForbiddenException;
import app.programmatic.ui.common.foros.service.ForosRestrictionService;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.model.RestrictionCommandBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RestrictionServiceImpl implements RestrictionService {
    @Autowired
    private ForosRestrictionService forosService;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.restriction.validation.ForosRestrictionViolationsServiceImpl")
    public boolean isPermittedAll(RestrictionCommandBuilder builder) {
        return isPermitted(builder).stream()
                .allMatch( predicate -> predicate );
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.restriction.validation.ForosRestrictionViolationsServiceImpl")
    public List<Boolean> isPermitted(RestrictionCommandBuilder builder) {
        return forosService.getRestrictionService().get(builder.build()).getPredicates();
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.restriction.validation.ForosRestrictionViolationsServiceImpl")
    public void throwIfNotPermitted(RestrictionCommandBuilder builder) {
        if (!isPermittedAll(builder)) {
            throw new ForbiddenException();
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.restriction.validation.ForosRestrictionViolationsServiceImpl")
    public void throwIfNotPermitted(Restriction restriction) {
        throwIfNotPermitted(restriction, null);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.restriction.validation.ForosRestrictionViolationsServiceImpl")
    public void throwIfNotPermitted(Restriction restriction, Long paramId) {
        RestrictionCommandBuilder builder = new RestrictionCommandBuilder();
        builder.add(restriction, paramId);
        throwIfNotPermitted(builder);
    }

    @Override
    public void throwIfNotCurrentUser(Long expectedUserId) {
        if (!authorizationService.getAuthUserInfo().getId().equals(expectedUserId)) {
            throw new ForbiddenException();
        }
    }
}
