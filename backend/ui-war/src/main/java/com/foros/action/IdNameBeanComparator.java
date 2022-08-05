package com.foros.action;

import com.foros.util.StringUtil;

import java.util.Comparator;

public class IdNameBeanComparator implements Comparator<IdNameBean> {
    public int compare(IdNameBean o1, IdNameBean o2) {
        if (o1 == null || o2 == null) {
            throw new NullPointerException("Can't compare null values");
        }
        return StringUtil.lexicalCompare(o1.getName(), o2.getName());
    }
}
