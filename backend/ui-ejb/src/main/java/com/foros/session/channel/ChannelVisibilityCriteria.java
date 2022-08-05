package com.foros.session.channel;

import com.foros.model.channel.ChannelVisibility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class ChannelVisibilityCriteria {
    //predefines
    private static final Map<ChannelVisibilityCriteria, ChannelVisibilityCriteria> predefineds = new HashMap<ChannelVisibilityCriteria, ChannelVisibilityCriteria>(5);

    public static final ChannelVisibilityCriteria NONE = createPredefined();
    public static final ChannelVisibilityCriteria ALL = createPredefined(ChannelVisibility.values());
    public static final ChannelVisibilityCriteria NON_PRIVATE = createPredefined(ChannelVisibility.PUB, ChannelVisibility.CMP);
    public static final ChannelVisibilityCriteria NON_CPM = createPredefined(ChannelVisibility.PUB, ChannelVisibility.PRI);
    public static final ChannelVisibilityCriteria PUBLIC = createPredefined(ChannelVisibility.PUB);
    public static final ChannelVisibilityCriteria PRIVATE = createPredefined(ChannelVisibility.PRI);
    public static final ChannelVisibilityCriteria CMP = createPredefined(ChannelVisibility.CMP);

    private static ChannelVisibilityCriteria createPredefined(ChannelVisibility... visibilities) {
        ChannelVisibilityCriteria cvc = valueOf(visibilities);
        predefineds.put(cvc, cvc);
        return cvc;
    }

    public static ChannelVisibilityCriteria valueOf(String string) {
        String[] strings = string.split(",");
        ChannelVisibility[] visibilities = new ChannelVisibility[strings.length];
        for(int i=0; i < strings.length; i++) {
            visibilities[i] = ChannelVisibility.valueOf(strings[i]);
        }
        return valueOf(visibilities);
    }

    public static ChannelVisibilityCriteria valueOf(ChannelVisibility... cmp) {
        ChannelVisibilityCriteria newCriteria = new ChannelVisibilityCriteria(cmp);
        ChannelVisibilityCriteria predefined = predefineds.get(newCriteria);
        if (predefined != null) {
            return predefined;
        } else {
            return newCriteria;
        }
    }

    private ChannelVisibility[] visibilities;

    ChannelVisibilityCriteria(ChannelVisibility... visibilities) {
        Arrays.sort(visibilities);
        this.visibilities = visibilities;
    }

    public Collection<ChannelVisibility> getVisibilities() {
        return Collections.unmodifiableCollection(Arrays.asList(visibilities));
    }

    public String getName() {
        return StringUtils.join(visibilities, ',');
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelVisibilityCriteria that = (ChannelVisibilityCriteria) o;

        if (!Arrays.equals(visibilities, that.visibilities)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(visibilities);
    }
}
