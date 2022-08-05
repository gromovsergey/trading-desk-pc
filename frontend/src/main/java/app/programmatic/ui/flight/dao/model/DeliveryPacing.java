package app.programmatic.ui.flight.dao.model;

public enum DeliveryPacing {
    U("Unrestricted"),
    F("Fixed"),
    D("Dynamic")
    ;

    private String description;

    DeliveryPacing(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
