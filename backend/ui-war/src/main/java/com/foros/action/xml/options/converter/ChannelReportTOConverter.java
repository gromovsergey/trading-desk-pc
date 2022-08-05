package com.foros.action.xml.options.converter;

import com.foros.session.channel.ChannelReportTO;
import com.foros.util.PairUtil;

public class ChannelReportTOConverter extends AbstractConverter<ChannelReportTO> {
    public ChannelReportTOConverter(boolean concatForValue) {
        super(concatForValue);
    }

    @Override
    protected String getName(ChannelReportTO value) {
        return value.getName();
    }

    @Override
    protected String getValue(ChannelReportTO value) {
        return value.getId() == null ? "" : PairUtil.createAsString(value.getId(), value.getChannelType().toString());
    }

}
