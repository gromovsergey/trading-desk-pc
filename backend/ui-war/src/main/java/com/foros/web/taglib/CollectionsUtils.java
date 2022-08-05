package com.foros.web.taglib;

import com.foros.action.IdNameBean;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CollectionsUtils {

    private CollectionsUtils() {
    }

    public static boolean contains(Collection collection, Object item) {
        return collection.contains(item);
    }

    public static Collection convertEnums(Collection<Enum> enums) {
        List<IdNameBean> arrayList = new ArrayList<IdNameBean>();

        if (enums == null || enums.isEmpty()) {
            return arrayList;
        }

        for (Enum anEnum : enums) {
            arrayList.add(new IdNameBean(anEnum.name(),
                    StringUtil.getLocalizedString("enums." + anEnum.getClass().getSimpleName() + "." + anEnum.name())));
        }
        return arrayList;
    }
}
