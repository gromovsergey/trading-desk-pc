package app.programmatic.ui.flight.dao.model;

public enum TargetingPacing {
    U("Unrestricted"),
    F("Fixed"),
    D("Dynamic")
    ;

    private String description;

    TargetingPacing(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
