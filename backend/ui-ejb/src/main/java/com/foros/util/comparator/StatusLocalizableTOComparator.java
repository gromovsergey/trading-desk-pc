package com.foros.util.comparator;

import com.foros.session.EntityTO;

import java.util.Comparator;

public class StatusLocalizableTOComparator<TO extends EntityTO> extends StatusTOComparatorBase<TO> {

    public static final Comparator<EntityTO> INSTANCE = new StatusLocalizableTOComparator<>();

    @Override
    protected int otherCompare(TO to1, TO to2) {
        return LocalizableTOComparator.INSTANCE.compare(to1, to2);
    }
}
