package com.foros.framework.conversion;

import com.foros.model.Identifiable;
import com.foros.model.channel.GenericChannel;

public class ChannelConverter extends IdentifiableEntityConverterSupport {
    @Override
    protected Identifiable newInstance(Class toClass) {
        return new GenericChannel();
    }
}
