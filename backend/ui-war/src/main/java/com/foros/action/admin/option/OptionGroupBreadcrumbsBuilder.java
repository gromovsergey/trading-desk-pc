package com.foros.action.admin.option;

import com.foros.action.admin.creativeSize.CreativeSizeBreadcrumbsElement;
import com.foros.action.admin.creativeSize.CreativeSizesBreadcrumbsElement;
import com.foros.action.admin.template.creative.CreativeTemplateBreadcrumbsElement;
import com.foros.action.admin.template.creative.CreativeTemplatesBreadcrumbsElement;
import com.foros.action.admin.template.discover.DiscoverTemplateBreadcrumbsElement;
import com.foros.action.admin.template.discover.DiscoverTemplatesBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;

public class OptionGroupBreadcrumbsBuilder{

    public Breadcrumbs build(OptionGroup optionGroup) {
        RelatedType relatedType = findRelatedType(optionGroup);
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        switch (relatedType) {
            case CREATIVE_TEMPLATE: {
                Template template = optionGroup.getTemplate();
                breadcrumbs.add(new CreativeTemplatesBreadcrumbsElement()).add(new CreativeTemplateBreadcrumbsElement(template));
                break;
            }
            case DISCOVER_TEMPLATE: {
                Template template = optionGroup.getTemplate();
                breadcrumbs.add(new DiscoverTemplatesBreadcrumbsElement()).add(new DiscoverTemplateBreadcrumbsElement(template));
                break;
            }
            case CREATIVE_SIZE:
                CreativeSize size = optionGroup.getCreativeSize();
                breadcrumbs.add(new CreativeSizesBreadcrumbsElement()).add(new CreativeSizeBreadcrumbsElement(size));
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (optionGroup.getId() != null) {
            breadcrumbs.add(new OptionGroupBreadcrumbsElement(optionGroup));
        }
        return breadcrumbs;
    }

    private RelatedType findRelatedType(OptionGroup optionGroup) {
        if (optionGroup.getTemplate() != null) {
            Template template = optionGroup.getTemplate();
            if (template != null) {
                if (template instanceof CreativeTemplate) {
                    return RelatedType.CREATIVE_TEMPLATE;
                } else if (template instanceof DiscoverTemplate) {
                    return RelatedType.DISCOVER_TEMPLATE;
                }
            }
        } else if (optionGroup.getCreativeSize() != null) {
            return RelatedType.CREATIVE_SIZE;
        }
        return null;
    }
}
