package com.foros.util.comparator;

import com.foros.cache.NamedCO;
import com.foros.util.StringUtil;
import com.foros.util.messages.MessageProvider;
import com.foros.util.TimezoneHelper;
import com.foros.util.Resolver;

import java.util.Comparator;

/**
     * Comparator for countries, compares by sortOrder if it is defined and by resolved name if not.
 */
public class SimpleResolveComparator<T> extends AbstractResolveComparator<NamedCO<T>> {

    public SimpleResolveComparator(Resolver resolver) {
        super(resolver);
    }

    public int compare(NamedCO<T> o1, NamedCO<T> o2) {
        String name1 = resolver.resolve(o1.getName());
        String name2 = resolver.resolve(o2.getName());
        return StringUtil.compareToIgnoreCase(name1, name2);
    }

}
