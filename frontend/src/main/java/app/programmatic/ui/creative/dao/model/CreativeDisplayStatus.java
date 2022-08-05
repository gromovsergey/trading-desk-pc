package app.programmatic.ui.creative.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.HashMap;
import java.util.Map;


public enum CreativeDisplayStatus {
    LIVE          (1, MajorDisplayStatus.LIVE, "creative.displaystatus.live"),
    DECLINED      (2, MajorDisplayStatus.NOT_LIVE, "creative.displaystatus.declined"),
    PENDING_FOROS (3, MajorDisplayStatus.NOT_LIVE, "creative.displaystatus.pending_foros"),
    PENDING_USER  (4, MajorDisplayStatus.NOT_LIVE, "creative.displaystatus.pending_user"),
    INACTIVE      (5, MajorDisplayStatus.INACTIVE, "creative.displaystatus.inactive"),
    DELETED       (6, MajorDisplayStatus.DELETED, "creative.displaystatus.deleted");

    private static Map<Integer, CreativeDisplayStatus> displayStatuses = initDisplayStatusMap();

    private int id;
    private MajorDisplayStatus majorStatus;
    private String descriptionKey;

    CreativeDisplayStatus(int id, MajorDisplayStatus majorStatus, String descriptionKey) {
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

    public static CreativeDisplayStatus valueOf(Integer id) {
        return displayStatuses.get(id);
    }

    private static Map<Integer, CreativeDisplayStatus> initDisplayStatusMap() {
        Map<Integer, CreativeDisplayStatus> result = new HashMap<>(CreativeDisplayStatus.values().length);
        for (CreativeDisplayStatus status : CreativeDisplayStatus.values()) {
            result.put(status.getId(), status);
        }
        return result;
    }
}
