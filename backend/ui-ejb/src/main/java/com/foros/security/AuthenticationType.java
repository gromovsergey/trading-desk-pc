package com.foros.security;

public enum AuthenticationType {
    PSWD("PSWD"),
    LDAP("LDAP"),
    NONE("NONE");

    private final String name;

    private AuthenticationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AuthenticationType byName(String name) {
        for (AuthenticationType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal name given: '" + name + "'");
    }
}
