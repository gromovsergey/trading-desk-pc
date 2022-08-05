package com.foros.session.template;

import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.site.Tag;
import com.foros.model.site.TagOptionValue;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.Template;
import com.foros.session.ServiceLocator;
import com.foros.session.cache.CacheService;

public class OptionHelper {

    public static void evictCreatives() {
        // evictNonTransactional Creative option values
        CacheService cacheService = ServiceLocator.getInstance().lookup(CacheService.class);
        cacheService.evictCollection(Creative.class, "options");
        cacheService.evictCollection(Creative.class, "groupStates");
        cacheService.evictRegion(CreativeOptionValue.class);
        cacheService.evictRegion(CreativeOptGroupState.class);
    }

    public static void evictWDTags() {
        // evictNonTransactional WDTag option values
        CacheService cacheService = ServiceLocator.getInstance().lookup(CacheService.class);
        cacheService.evictCollection(WDTag.class, "options");
        cacheService.evictRegion(WDTagOptionValue.class);
    }

    public static void evictTags() {
        // evictNonTransactional Tag option values
        CacheService cacheService = ServiceLocator.getInstance().lookup(CacheService.class);
        cacheService.evictCollection(Tag.class, "options");
        cacheService.evictRegion(TagOptionValue.class);
    }

    public static void evictCache(OptionGroup optionGroup) {
        OptionGroupType type = optionGroup.getType();
        Template template = optionGroup.getTemplate();
        if (OptionGroupType.Publisher == type) {
            if (template instanceof DiscoverTemplate) {
                OptionHelper.evictWDTags();
            } else {
                OptionHelper.evictTags();
            }
        } else {
            OptionHelper.evictCreatives();
            OptionHelper.evictTags();
        }
    }
}
