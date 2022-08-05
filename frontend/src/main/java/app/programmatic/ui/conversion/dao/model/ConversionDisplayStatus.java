package app.programmatic.ui.conversion.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.HashMap;
import java.util.Map;


public enum ConversionDisplayStatus {
    LIVE          (1, MajorDisplayStatus.LIVE, "action.displaystatus.live"),
    DELETED       (2, MajorDisplayStatus.DELETED, "action.displaystatus.deleted"),
    LIVE_NEED_ATT (3, MajorDisplayStatus.LIVE_NEED_ATT, "action.displaystatus.live_na"),
    NOT_LIVE      (4, MajorDisplayStatus.NOT_LIVE, "action.displaystatus.not_live");

    private static Map<Integer, ConversionDisplayStatus> displayStatuses = initDisplayStatusMap();

    private int id;
    private MajorDisplayStatus majorStatus;
    private String descriptionKey;

    ConversionDisplayStatus(int id, MajorDisplayStatus majorStatus, String descriptionKey) {
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

    public static ConversionDisplayStatus valueOf(Integer id) {
        return displayStatuses.get(id);
    }

    private static Map<Integer, ConversionDisplayStatus> initDisplayStatusMap() {
        Map<Integer, ConversionDisplayStatus> result = new HashMap<>(ConversionDisplayStatus.values().length);
        for (ConversionDisplayStatus status : ConversionDisplayStatus.values()) {
            result.put(status.getId(), status);
        }
        return result;
    }
}
