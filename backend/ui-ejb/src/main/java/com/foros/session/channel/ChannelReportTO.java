package com.foros.session.channel;

import com.foros.session.EntityTO;

public class ChannelReportTO extends EntityTO {
    
    private char channelType;
    
    public ChannelReportTO() {
        super();
    }
    
    public ChannelReportTO(Long id, String name, char status, String channelType) {
        super(id, name, status);
        this.channelType = channelType.charAt(0);
    }

    public Character getChannelType() {
        return channelType;
    }

    public void setChannelType(Character channelType) {
        this.channelType = channelType;
    }
    
}
