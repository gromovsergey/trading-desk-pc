package com.foros.session.template;

import com.foros.model.LocalizableName;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.Option;
import com.foros.util.LocalizableNameUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class OptionComparator implements Comparator<Option> {
    private Comparator<LocalizableName> comparator;
    private static final Map<String, Integer> MUST_BE_FIRST = new HashMap<String, Integer>();

    static {
        MUST_BE_FIRST.put(CreativeToken.WIDTH.getName(), 0);
        MUST_BE_FIRST.put(CreativeToken.HEIGHT.getName(), 1);
        MUST_BE_FIRST.put(CreativeToken.CRCLICK.getName(), 2);
    }

    public OptionComparator() {
        comparator = LocalizableNameUtil.getComparator();
    }

    @Override
    public int compare(Option option1, Option option2) {
        if (option1 == null || option2 == null) {
            throw new NullPointerException("Can't compare null options");
        }

        String token1 = option1.getToken();
        String token2 = option2.getToken();

        if (MUST_BE_FIRST.containsKey(token1) && MUST_BE_FIRST.containsKey(token2)) {
            return MUST_BE_FIRST.get(token1) - MUST_BE_FIRST.get(token2);
        }

        if (MUST_BE_FIRST.containsKey(token1)) {
            return -1;
        }

        if (MUST_BE_FIRST.containsKey(token2)) {
            return 1;
        }
        return comparator.compare(option1.getName(), option2.getName());
    }
}
