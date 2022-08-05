package com.foros.action.admin.creativeSize;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.creative.SizeType;

public class SizeTypeBreadcrumbsElement extends EntityBreadcrumbsElement {
    public SizeTypeBreadcrumbsElement(SizeType sizeType) {
        super("SizeType.breadcrumbs", sizeType.getId(), sizeType.getDefaultName(), "SizeType/view");
    }
}
