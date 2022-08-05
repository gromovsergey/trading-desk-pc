package com.foros.breadcrumbs;

public class SimpleTextBreadcrumbsElement implements BreadcrumbsElement {
    private String resource;

    public SimpleTextBreadcrumbsElement(ActionBreadcrumbs action) {
        this.resource = "form." + action.getText();
    }

    public SimpleTextBreadcrumbsElement(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
