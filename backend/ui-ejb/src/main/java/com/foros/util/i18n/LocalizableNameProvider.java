package com.foros.util.i18n;

import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;

public enum LocalizableNameProvider {

    CREATIVE_SIZE(CreativeSize.class, "CreativeSize"),
    CREATIVE_CATEGORY(CreativeCategory.class, "CreativeCategory"),
    OPTION(Option.class, "Option-name"),
    OPTION_GROUP(OptionGroup.class, "OptionGroup-name"),
    TEMPLATE(Template.class, "Template"),
    SIZE_TYPE(SizeType.class, "SizeType");

    private String prefix;

    private Class<?> clazz;

    LocalizableNameProvider(Class<? extends LocalizableNameEntity> clazz, String prefix) {
        this.clazz = clazz;
        this.prefix = prefix;
    }

    public LocalizableName provide(String defaultName, Object id) {
        String resourceKey = getResourceKey(id);
        return new LocalizableName(defaultName, resourceKey);
    }

    public String getResourceKey(Object id) {
        return prefix + "." + id;
    }

    public static LocalizableNameProvider valueOf(Class<?> clazz) {
        for (LocalizableNameProvider provider : LocalizableNameProvider.values()) {
            if (clazz.equals(provider.clazz))
                return provider;
        }
        return null;
    }
}
