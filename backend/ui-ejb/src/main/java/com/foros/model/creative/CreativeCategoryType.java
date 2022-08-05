package com.foros.model.creative;

public enum CreativeCategoryType {
    VISUAL("Visual"),
    CONTENT("Content"),
    TAG("Tag");
    
    private final String name;

    CreativeCategoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CreativeCategoryType byName(String name) {
        for (CreativeCategoryType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Illegal name given: '" + name + "'");
    }
}
