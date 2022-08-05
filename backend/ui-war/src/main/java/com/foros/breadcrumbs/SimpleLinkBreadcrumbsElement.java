package com.foros.breadcrumbs;

public class SimpleLinkBreadcrumbsElement extends SimpleTextBreadcrumbsElement {

    private String path;

    public SimpleLinkBreadcrumbsElement(String resource, String path) {
        super(resource);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
