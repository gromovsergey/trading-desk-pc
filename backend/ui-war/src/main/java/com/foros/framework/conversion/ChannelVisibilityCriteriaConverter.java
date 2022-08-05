package com.foros.framework.conversion;

import com.foros.session.channel.ChannelVisibilityCriteria;

import java.util.Map;

public class ChannelVisibilityCriteriaConverter extends SingleValueBaseTypeConverter {

    @Override
    public Object convertFromString(Map context, String value, Class toClass) {
        return ChannelVisibilityCriteria.valueOf(value);
    }

    @Override
    public String convertToString(Map context, Object o) {
        return ((ChannelVisibilityCriteria)o).getName();
    }
}
