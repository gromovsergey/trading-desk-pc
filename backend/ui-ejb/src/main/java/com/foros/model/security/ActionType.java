package com.foros.model.security;

public enum ActionType {
    CREATE(0, "Create", false),
    UPDATE(1, "Update", false),
    LOGIN(2, "Login", true),
    APPROVE(3, "Approve", false),
    DELETE(7, "Delete", false),
    REFRESH_DISPLAY_STATUS(9, "Refresh Display Status", false),
    COMPLETE_REPORT(10, "Complete Report", false),
    START_REPORT(11, "Start Report", false);

    private Integer id;
    private String name;
    private boolean objectless;
    
    ActionType(Integer id, String name, boolean objectless) {
        this.id = id;
        this.name = name;
        this.objectless = objectless;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isObjectless() {
        return objectless;
    }

    public static ActionType valueOf(Integer id){
        for (ActionType type : ActionType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }

    public static ActionType findByName(String name) {
        for (ActionType type : ActionType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal Action Type name: '" + name + "'");
    }

    public String toString() {
        return name().replaceAll("\\_", " ");
    }
}
