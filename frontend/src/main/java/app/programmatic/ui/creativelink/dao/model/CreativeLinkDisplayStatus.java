package app.programmatic.ui.creativelink.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.HashMap;
import java.util.Map;


public enum CreativeLinkDisplayStatus {
    LIVE    (1, MajorDisplayStatus.LIVE, "campaigncreative.displaystatus.live"),
    NOT_LIVE(2, MajorDisplayStatus.NOT_LIVE, "campaigncreative.displaystatus.not_live"),
    INACTIVE(3, MajorDisplayStatus.INACTIVE, "campaigncreative.displaystatus.inactive"),
    DELETED (4, MajorDisplayStatus.DELETED, "campaigncreative.displaystatus.deleted");

    private static Map<Integer, CreativeLinkDisplayStatus> displayStatuses = initDisplayStatusMap();

    private int id;
    private MajorDisplayStatus majorStatus;
    private String descriptionKey;

    CreativeLinkDisplayStatus(int id, MajorDisplayStatus majorStatus, String descriptionKey) {
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

    public static CreativeLinkDisplayStatus valueOf(Integer id) {
        return displayStatuses.get(id);
    }

    private static Map<Integer, CreativeLinkDisplayStatus> initDisplayStatusMap() {
        Map<Integer, CreativeLinkDisplayStatus> result = new HashMap<>(CreativeLinkDisplayStatus.values().length);
        for (CreativeLinkDisplayStatus status : CreativeLinkDisplayStatus.values()) {
            result.put(status.getId(), status);
        }
        return result;
    }
}
