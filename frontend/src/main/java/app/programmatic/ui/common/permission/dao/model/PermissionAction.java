package app.programmatic.ui.common.permission.dao.model;


public enum PermissionAction {
    CREATE("create"),
    EDIT("edit"),
    VIEW("view"),
    RUN("run"),
    EDIT_ALLOCATIONS("edit_allocations"),
    VIEW_SYSTEM_FINANCE("view_system_finance");

    private final String storedValue;

    PermissionAction(String storedValue) {
        this.storedValue = storedValue;
    }

    public String getStoredValue() {
        return storedValue;
    }

    public static PermissionAction findByStoredValue(String value) {
        for (PermissionAction action : PermissionAction.values()) {
            if (action.getStoredValue().equals(value)) {
                return action;
            }
        }

        throw new IllegalArgumentException("Illegal Action Type value: '" + value + "'");
    }
}
