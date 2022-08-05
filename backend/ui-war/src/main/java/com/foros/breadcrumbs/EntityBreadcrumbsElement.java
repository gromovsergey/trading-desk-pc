package com.foros.breadcrumbs;

public class EntityBreadcrumbsElement implements BreadcrumbsElement {
    private String entityNameResource;
    private Object id;
    private String name;
    private String path;

    protected EntityBreadcrumbsElement(String entityTypeName, Object id, String name, String path) {
        this.entityNameResource = entityTypeName;
        this.id = id;
        this.name = name;
        this.path = path;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getEntityNameResource() {
        return entityNameResource;
    }

    public Object getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
