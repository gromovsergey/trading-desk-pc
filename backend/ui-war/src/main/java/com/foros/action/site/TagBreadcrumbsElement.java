package com.foros.action.site;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.site.Tag;

public class TagBreadcrumbsElement extends EntityBreadcrumbsElement {
    public TagBreadcrumbsElement(Tag tag) {
        super("site.breadcrumbs.tag", tag.getId(), tag.getName(), "tag/view");
    }
}
