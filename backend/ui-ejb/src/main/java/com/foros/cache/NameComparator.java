package com.foros.cache;

import com.foros.util.StringUtil;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author pavel
 * Created on September 14, 2006, 5:03 PM
 */
public class NameComparator implements Comparator<NamedCO>, Serializable {
    public int compare(NamedCO o1, NamedCO o2) {
        int res = StringUtil.lexicalCompare(o1.getName(), o2.getName());
        if (res == 0) {
            Integer id1 = o1.getId().hashCode();
            res = id1.compareTo(o2.getId().hashCode());
        }

        return res;
    }
}
