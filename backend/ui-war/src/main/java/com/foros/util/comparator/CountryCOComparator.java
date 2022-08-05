package com.foros.util.comparator;

import com.foros.cache.application.CountryCO;
import com.foros.util.Resolver;
import com.foros.util.StringUtil;

/**
 * Comparator for countries, compares by sortOrder if it is defined and by resolved name if not.
 */
public class CountryCOComparator extends AbstractResolveComparator<CountryCO> {

    public CountryCOComparator(Resolver resolver) {
        super(resolver);
    }

    public int compare(CountryCO o1, CountryCO o2) {
        if (o1.getSortOrder() != null && o2.getSortOrder() != null) {
            if (o1.getSortOrder().intValue() !=  o2.getSortOrder().intValue()) {
                return o1.getSortOrder().compareTo(o2.getSortOrder());
            }
        } else if (o1.getSortOrder() != null) {
            return -1;
        } else if (o2.getSortOrder() != null) {
            return 1;
        }

        String name1 = resolver.resolve(o1.getId());
        String name2 = resolver.resolve(o2.getId());
        return StringUtil.compareToIgnoreCase(name1, name2);
    }
}
