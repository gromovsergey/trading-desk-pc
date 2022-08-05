package com.foros.util.comparator;

import com.foros.model.IdNameEntity;
import com.foros.util.StringUtil;

import java.util.Comparator;

public class IdNameComparator implements Comparator<IdNameEntity> {

    public int compare(IdNameEntity o1, IdNameEntity o2) {
        return StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
    }

}
