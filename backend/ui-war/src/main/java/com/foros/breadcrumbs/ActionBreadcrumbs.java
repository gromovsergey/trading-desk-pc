package com.foros.breadcrumbs;

public enum ActionBreadcrumbs {
    CREATE("createNew"),
    EDIT("edit"),
    VIEW_LOG("viewLog");

    private String text;

    private ActionBreadcrumbs(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
