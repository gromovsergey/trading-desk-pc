package com.foros.util;

import com.foros.cache.NamedCO;
import com.foros.util.comparator.SimpleResolveComparator;
import com.foros.util.messages.MessageProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vanin Boris
 *
 */
public class TimezoneHelper {

    private TimezoneHelper() {}

    /**
     * Create and return comparator for timezones(CO) for ordering by sortOrder, name
     *
     * @param provider message provider
     * @return comparator
     */
    private static Comparator<NamedCO<Long>> createTimezoneComparator(MessageProvider provider) {
        return new SimpleResolveComparator<Long>(getResolver(provider));
    }

    private static FormatResolver getResolver(MessageProvider provider) {
        return new FormatResolver(provider, "global.timezone.{0}.name", true);
    }

    /**
     * Create duplicate of collection of countries and sort it
     *
     * @param timezones collection of timezones
     * @param provider message provider
     * @return sorted list of countries
     */
    public static List<NamedCO<Long>> sort(Collection<NamedCO<Long>> timezones, MessageProvider provider) {
        List<NamedCO<Long>> result = new LinkedList<NamedCO<Long>>(timezones);
        Collections.sort(result, createTimezoneComparator(provider));
        return result;
    }

    public static String resolve(String key) {
        return getResolver(MessageProvider.createMessageProviderAdapter()).resolve(key);
    }

}
