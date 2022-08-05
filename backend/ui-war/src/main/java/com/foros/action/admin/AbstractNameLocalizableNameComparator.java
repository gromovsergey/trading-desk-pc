package com.foros.action.admin;

import com.foros.model.IdNameEntity;
import com.foros.model.LocalizableName;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.comparator.IdNameComparator;

import java.util.Comparator;

public abstract class AbstractNameLocalizableNameComparator<T extends IdNameEntity> implements Comparator<T> {
    private Comparator<IdNameEntity> nameComparator = new IdNameComparator();
    private Comparator<LocalizableName> sizeNameComparator = LocalizableNameUtil.getComparator();

    public final int compare(T o1, T o2) {
        int res = nameComparator.compare(o1, o2);
        if (res == 0) {
            res = compareLocalizableNames(sizeNameComparator, o1, o2);
        }
        return res;
    }

    protected abstract int compareLocalizableNames(Comparator<LocalizableName> comparator, T o1, T o2);
}
