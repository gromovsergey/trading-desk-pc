package com.foros.session.channel;

import com.foros.model.DisplayStatus;

public class DeviceChannelTO extends ChannelTO {
    private DisplayStatus accountStatus;

    private int level;

    public DeviceChannelTO() {
        super();
    }

    public DeviceChannelTO(Long id, String name, char status, char qaStatus, Long displayStatusId, int level) {
        super(id, name, status, qaStatus, displayStatusId, "V");
        this.level = level;
    }

    public DisplayStatus getAccountStatus() {
        return accountStatus;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public Character getChannelType() {
        return 'V';
    }
}
