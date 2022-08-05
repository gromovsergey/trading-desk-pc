package com.foros.action.admin;

public class DuplicateCategoryException extends RuntimeException {
    private String name;
    public DuplicateCategoryException(String name) {
        super("Name: " + name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
