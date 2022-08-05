package com.foros.util.comparator;

import com.foros.session.EntityTO;
import com.foros.util.StringUtil;

import java.util.Comparator;

public class StatusNameTOComparator<TO extends EntityTO> extends StatusTOComparatorBase<TO> {

    public static final Comparator<EntityTO> INSTANCE = new StatusNameTOComparator<>();

    @Override
    protected int otherCompare(TO to1, TO to2) {
        return StringUtil.lexicalCompare(to1.getName(), to2.getName());
    }
}
