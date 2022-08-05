package app.programmatic.ui.channel.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.HashMap;
import java.util.Map;


public enum ChannelDisplayStatus {
    LIVE                            (1, MajorDisplayStatus.LIVE, "channel.displaystatus.live"),
    DECLINED                        (2, MajorDisplayStatus.NOT_LIVE, "channel.displaystatus.declined"),
    PENDING_FOROS                   (3, MajorDisplayStatus.NOT_LIVE, "channel.displaystatus.pending_foros"),
    INACTIVE                        (4, MajorDisplayStatus.INACTIVE, "channel.displaystatus.inactive"),
    DELETED                         (5, MajorDisplayStatus.DELETED, "channel.displaystatus.deleted"),
    LIVE_PENDING_INACTIVATION       (6, MajorDisplayStatus.LIVE, "channel.displaystatus.live_pending_inactivation"),
    NOT_LIVE_CHANNELS_NEED_ATT      (7, MajorDisplayStatus.NOT_LIVE, "channel.displaystatus.not_live_channels_na"),
    NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS(8, MajorDisplayStatus.NOT_LIVE, "channel.displaystatus.not_live_not_enough_users"),//+,"channel.displaystatus.not_live_not_enough_users.external"),
    LIVE_CHANNELS_NEED_ATT          (9, MajorDisplayStatus.LIVE_NEED_ATT, "channel.displaystatus.live_channels_na"),
    LIVE_TRIGGERS_NEED_ATT          (10, MajorDisplayStatus.LIVE_NEED_ATT, "channel.displaystatus.live_triggers_na"),
    LIVE_AMBER_PENDING_INACTIVATION (11, MajorDisplayStatus.LIVE_NEED_ATT, "channel.displaystatus.live_pending_inactivation_na");

    private static Map<Integer, ChannelDisplayStatus> displayStatuses = initDisplayStatusMap();

    private int id;
    private MajorDisplayStatus majorStatus;
    private String descriptionKey;

    ChannelDisplayStatus(int id, MajorDisplayStatus majorStatus, String descriptionKey) {
        this.id = id;
        this.majorStatus = majorStatus;
        this.descriptionKey = descriptionKey;
    }

    public int getId() {
        return id;
    }

    public MajorDisplayStatus getMajorStatus() {
        return majorStatus;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public static ChannelDisplayStatus valueOf(Integer id) {
        return displayStatuses.get(id);
    }

    private static Map<Integer, ChannelDisplayStatus> initDisplayStatusMap() {
        Map<Integer, ChannelDisplayStatus> result = new HashMap<>(ChannelDisplayStatus.values().length);
        for (ChannelDisplayStatus status : ChannelDisplayStatus.values()) {
            result.put(status.getId(), status);
        }
        return result;
    }
}
