package com.foros.session;

import static com.foros.util.StringUtil.lexicalCompare;

import java.util.Comparator;

public class NameTOComparator<T extends NamedTO> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        if (o1 == null || o2 == null) {
            throw new NullPointerException("Can't compare null values");
        }
        return lexicalCompare(o1.getName(), o2.getName());
    }
}
