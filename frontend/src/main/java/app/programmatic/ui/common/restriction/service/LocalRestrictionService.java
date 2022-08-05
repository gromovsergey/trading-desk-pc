package app.programmatic.ui.common.restriction.service;

public interface LocalRestrictionService {
    boolean isAllowed(String name, Object... parameters);
}
