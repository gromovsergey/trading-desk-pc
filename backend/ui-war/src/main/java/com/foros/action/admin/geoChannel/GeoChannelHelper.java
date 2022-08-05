package com.foros.action.admin.geoChannel;

import com.foros.model.AddressField;
import com.foros.model.Country;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoType;
import com.foros.util.EntityUtils;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.StringUtil;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class GeoChannelHelper {

    private static final String CHANNEL_STATE = "channel.state";
    private static final String CHANNEL_CITY = "channel.city";
    private static final String STATE = "State";
    private static final String CITY = "City";
    private Country country;

    public GeoChannelHelper(Country country) {
        this.country = country;
    }

    public String getStateLabel() {
        for (AddressField addressField : country.getAddressFields()) {
            if (STATE.equals(addressField.getOFFieldName())) {
                return LocalizableNameUtil.getLocalizedValue(addressField.getName());
            }
        }
        return StringUtil.getLocalizedString(CHANNEL_STATE);
    }

    public String getCityLabel() {
        for (AddressField addressField : country.getAddressFields()) {
            if (CITY.equals(addressField.getOFFieldName())) {
                return LocalizableNameUtil.getLocalizedValue(addressField.getName());
            }
        }
        return StringUtil.getLocalizedString(CHANNEL_CITY);
    }

    public static TreeSet<GeoChannel> appendStatusSuffixAndSortForGeoTarget(Set<GeoChannel> channels) {
        appendStatusSuffix(channels);
        return sortGeoTarget(channels);
    }

    public static TreeSet<GeoChannel> sortGeoTarget(Set<GeoChannel> channels) {
        TreeSet<GeoChannel> sorted = new TreeSet<>(new Comparator<GeoChannel>() {
            @Override
            public int compare(GeoChannel o1, GeoChannel o2) {
                return StringUtil.lexicalCompare(fullName(o1), fullName(o2));
            }

            private String fullName(GeoChannel channel) {
                StringBuilder sb = new StringBuilder(100);
                if (channel.getGeoType() == GeoType.ADDRESS) {
                    sb.append('\uFFFF').append(channel.getAddress()).append(channel.getRadius().getDistance()).append(channel.getRadius().getRadiusUnit());
                } else {
                    if (channel.getStateChannel() != null) {
                        sb.append(channel.getStateChannel().getName());
                        sb.append('\n');
                    }
                    sb.append(channel.getName());
                }
                return sb.toString();
            }
        });
        sorted.addAll(channels);
        return sorted;
    }

    public static void appendStatusSuffix(Set<GeoChannel> channels) {
        for (GeoChannel channel : channels) {
            channel.setName(EntityUtils.appendStatusSuffix(channel.getName(), channel.getStatus()));
        }
    }
}
