package com.foros.breadcrumbs;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Breadcrumbs implements Iterable<BreadcrumbsElement> {
    List<BreadcrumbsElement> elements = new LinkedList<BreadcrumbsElement>();

    public Breadcrumbs add(BreadcrumbsElement element) {
        elements.add(element);
        return this;
    }

    @Override
    public Iterator<BreadcrumbsElement> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    public Breadcrumbs add(ActionBreadcrumbs action) {
        BreadcrumbsElement element = new SimpleTextBreadcrumbsElement(action);
        return add(element);
    }

    public Breadcrumbs add(String resource) {
        BreadcrumbsElement element = new SimpleTextBreadcrumbsElement(resource);
        return add(element);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }
}
