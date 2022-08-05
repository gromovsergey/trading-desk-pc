package com.foros.action.admin.channel;

import com.foros.model.DisplayStatus;
import com.foros.model.channel.Channel;

public enum ChannelSearchStatus {
    ALL("displaystatus.major.all",
            null),
    ALL_HIDE_DELETED("displaystatus.major.all",
            new DisplayStatus[] { Channel.LIVE, Channel.DECLINED, Channel.PENDING_FOROS, Channel.INACTIVE, Channel.LIVE_PENDING_INACTIVATION, Channel.NOT_LIVE_CHANNELS_NEED_ATT, Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS, Channel.LIVE_CHANNELS_NEED_ATT,
                    Channel.LIVE_TRIGGERS_NEED_ATT, Channel.LIVE_AMBER_PENDING_INACTIVATION }),
    ALL_BUT_DELETED("displaystatus.major.all_but_deleted",
            new DisplayStatus[] { Channel.LIVE, Channel.DECLINED, Channel.PENDING_FOROS, Channel.INACTIVE, Channel.LIVE_PENDING_INACTIVATION, Channel.NOT_LIVE_CHANNELS_NEED_ATT, Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS, Channel.LIVE_CHANNELS_NEED_ATT,
                    Channel.LIVE_TRIGGERS_NEED_ATT, Channel.LIVE_AMBER_PENDING_INACTIVATION }),
    LIVE("displaystatus.major.live",
            new DisplayStatus[] { Channel.LIVE, Channel.LIVE_PENDING_INACTIVATION, Channel.LIVE_CHANNELS_NEED_ATT, Channel.LIVE_TRIGGERS_NEED_ATT, Channel.LIVE_AMBER_PENDING_INACTIVATION}),
    NOT_LIVE("displaystatus.major.not_live",
            new DisplayStatus[] { Channel.DECLINED, Channel.PENDING_FOROS, Channel.NOT_LIVE_CHANNELS_NEED_ATT, Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS }),
    NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS("channel.displaystatus.not_live_not_enough_users",
            new DisplayStatus[] { Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS }),
    DECLINED("displaystatus.major.declined",
            new DisplayStatus[] { Channel.DECLINED }),
    PENDING_FOROS("displaystatus.major.pending_foros",
            new DisplayStatus[] { Channel.PENDING_FOROS }),
    INACTIVE("displaystatus.major.inactive",
            new DisplayStatus[] { Channel.INACTIVE }),
    DELETED("displaystatus.major.deleted",
            new DisplayStatus[] { Channel.DELETED });

    private final String description;
    private final DisplayStatus[] displayStatuses;

    private ChannelSearchStatus(String description, DisplayStatus[] displayStatuses) {
        this.description = description;
        this.displayStatuses = displayStatuses;
    }

    public String getDescription() {
        return description;
    }

    public DisplayStatus[] getDisplayStatuses() {
        return displayStatuses;
    }

    public String getName() {
        return this.name();
    }


    public static DisplayStatus[] toDisplayStatuses(String name) {
        for (ChannelSearchStatus status : values()) {
            if (status.getName().equals(name)) {
                return status.getDisplayStatuses();
            }
        }
        return null;
    }
}
