package app.programmatic.ui.site.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.HashMap;
import java.util.Map;

public enum SiteDisplayStatus {
    LIVE          (1, MajorDisplayStatus.LIVE, "site.displaystatus.live"),
    NO_ACTIVE_TAGS(2, MajorDisplayStatus.NOT_LIVE, "site.displaystatus.no_active_tags"),
    DECLINED      (3, MajorDisplayStatus.NOT_LIVE, "site.displaystatus.declined"),
    PENDING_FOROS (4, MajorDisplayStatus.NOT_LIVE, "site.displaystatus.pending_foros"),
    DELETED       (6, MajorDisplayStatus.DELETED, "site.displaystatus.deleted");

    private static Map<Integer, SiteDisplayStatus> displayStatuses = initDisplayStatusMap();

    private int id;
    private MajorDisplayStatus majorStatus;
    private String descriptionKey;

    SiteDisplayStatus(int id, MajorDisplayStatus majorStatus, String descriptionKey) {
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

    public static SiteDisplayStatus valueOf(Integer id) {
        return displayStatuses.get(id);
    }

    private static Map<Integer, SiteDisplayStatus> initDisplayStatusMap() {
        Map<Integer, SiteDisplayStatus> result = new HashMap<>(SiteDisplayStatus.values().length);
        for (SiteDisplayStatus status : SiteDisplayStatus.values()) {
            result.put(status.getId(), status);
        }
        return result;
    }
}
