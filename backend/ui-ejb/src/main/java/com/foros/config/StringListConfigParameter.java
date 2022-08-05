package com.foros.config;

import com.foros.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringListConfigParameter extends AbstractConfigParameter<List<String>> {
    public StringListConfigParameter(String name) {
        super(name, null, true);
    }

    public StringListConfigParameter(String name, List<String> defaultValue) {
        super(name, defaultValue, true);
    }

    public StringListConfigParameter(String name, String... defaultValue) {
        super(name, Arrays.asList(defaultValue), true);
    }

    @Override
    public Class<List<String>> getType() {
        return (Class) List.class;
    }

    @Override
    public List<String> parse(String str) {
        if (str == null) {
            return Collections.EMPTY_LIST;
        }

        return Arrays.asList(StringUtil.splitByComma(str));
    }
}
