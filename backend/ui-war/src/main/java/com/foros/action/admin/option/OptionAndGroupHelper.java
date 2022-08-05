package com.foros.action.admin.option;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.Template;

import java.util.Set;

public class OptionAndGroupHelper {

    public static boolean isAdvertiserTypeEnabled(RelatedType relatedType, Long modelId, OptionGroupType modelType) {
        return relatedType != RelatedType.DISCOVER_TEMPLATE &&
                (modelId == null || modelType == OptionGroupType.Advertiser);
    }

    public static boolean isPublisherTypeEnabled(Long modelId, OptionGroupType modelType) {
        return modelId == null || modelType == OptionGroupType.Publisher;
    }

    public static boolean isHiddenTypeEnabled(Long modelId, OptionGroupType modelType) {
        return modelId == null || modelType == OptionGroupType.Hidden;
    }

    public static RelatedType getRelatedType(Template template, CreativeSize size) {
        if (template != null && template.getId() != null) {
            if (template instanceof CreativeTemplate) {
                return RelatedType.CREATIVE_TEMPLATE; }
            else if (template instanceof DiscoverTemplate) {
                return RelatedType.DISCOVER_TEMPLATE;
            }
        } else if (size != null && size.getId() != null) {
            return RelatedType.CREATIVE_SIZE;
        }
        return null;
    }

    public static Set<OptionGroup> getAdvertiserOptionGroups(Template template, CreativeSize size) {
        if (template != null && template.getId() != null) {
            return template.getAdvertiserOptionGroups();
        } else if (size != null && size.getId() != null) {
            return size.getAdvertiserOptionGroups();
        } else {
            throw new IllegalArgumentException("template or size is null");
        }
    }

    public static Set<OptionGroup> getPublisherOptionGroups(Template template, CreativeSize size) {
        if (template != null && template.getId() != null) {
            return template.getPublisherOptionGroups();
        } else if (size != null && size.getId() != null) {
            return size.getPublisherOptionGroups();
        } else {
            throw new IllegalArgumentException("template or size is null");
        }
    }

    public static Set<OptionGroup> getHiddenOptionGroups(Template template, CreativeSize size) {
        if (template != null && template.getId() != null) {
            return template.getHiddenOptionGroups();
        } else if (size != null && size.getId() != null) {
            return size.getHiddenOptionGroups();
        } else {
            throw new IllegalArgumentException("template or size is null");
        }
    }
}
