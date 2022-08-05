package com.foros.action.channel.bulk;

import com.foros.model.channel.ChannelVisibility;
import com.foros.util.StringUtil;
import com.foros.util.csv.BaseBulkHelper;

public class ChannelBulkHelper extends BaseBulkHelper {
    private static String CHANNEL_VISIBILITY_PRI = "Private";
    private static String CHANNEL_VISIBILITY_PUB = "Public";
    private static String CHANNEL_VISIBILITY_CMP = "CMP";

    public static String channelVisibilityToString(ChannelVisibility visibility) {
        if (visibility == null) {
            return null;
        }

        switch (visibility) {
            case PRI:
                return CHANNEL_VISIBILITY_PRI;
            case PUB:
                return CHANNEL_VISIBILITY_PUB;
            case CMP:
                return CHANNEL_VISIBILITY_CMP;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static ChannelVisibility stringToChannelVisibility(String visibility) {
        if (StringUtil.isPropertyEmpty(visibility)) {
            return null;
        }

        if (StringUtil.equalsWithIgnoreCase(CHANNEL_VISIBILITY_PRI, visibility)) {
            return ChannelVisibility.PRI;
        }
        if (StringUtil.equalsWithIgnoreCase(CHANNEL_VISIBILITY_PUB, visibility)) {
            return ChannelVisibility.PUB;
        }
        if (StringUtil.equalsWithIgnoreCase(CHANNEL_VISIBILITY_CMP, visibility)) {
            return ChannelVisibility.CMP;
        }
        throw new IllegalArgumentException();
    }
}