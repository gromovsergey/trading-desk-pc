package com.foros.session.creative;

import com.foros.model.creative.CreativeCategory;

import java.io.Serializable;
import java.util.Comparator;

public class CreativeCategoryComparator  implements Comparator<CreativeCategory>, Serializable {
    @Override
    public int compare(CreativeCategory first, CreativeCategory second) {
        return first.getDefaultName().compareTo(second.getDefaultName());
    }
}
