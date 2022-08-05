package com.foros.util.comparator;

import com.foros.session.EntityTO;
import com.foros.util.LocalizableNameUtil;

import java.util.Comparator;

public class LocalizableTOComparator<TO extends EntityTO> implements Comparator<TO> {

    public static final Comparator<EntityTO> INSTANCE = new LocalizableTOComparator<>();

    public int compare(TO to1, TO to2) {
        return LocalizableNameUtil.getComparator().compare(to1.getLocalizableName(), to2.getLocalizableName());
    }
}
