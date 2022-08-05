package app.programmatic.ui.restriction.service;

import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.model.RestrictionCommandBuilder;

import java.util.List;

public interface RestrictionService {
    boolean isPermittedAll(RestrictionCommandBuilder builder);

    List<Boolean> isPermitted(RestrictionCommandBuilder builder);

    void throwIfNotPermitted(RestrictionCommandBuilder builder);

    void throwIfNotPermitted(Restriction restriction);

    void throwIfNotPermitted(Restriction restriction, Long paramId);

    void throwIfNotCurrentUser(Long expectedUserId);
}
