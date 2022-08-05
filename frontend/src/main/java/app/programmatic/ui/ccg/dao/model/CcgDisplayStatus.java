package app.programmatic.ui.ccg.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.HashMap;
import java.util.Map;

public enum CcgDisplayStatus {
    LIVE                                     (1, MajorDisplayStatus.LIVE, "creativegroup.displaystatus.live"),
    LIVE_LINKED_CREATIVE_NEED_ATT            (2,  MajorDisplayStatus.LIVE_NEED_ATT, "creativegroup.displaystatus.live_linked_creatives_n_a"),
    LIVE_KEYWORDS_NEED_ATT                   (3,  MajorDisplayStatus.LIVE_NEED_ATT, "creativegroup.displaystatus.live_keywords_n_a"),
    LIVE_LINKED_CREATIVES_AND_KW_NEED_ATT    (4,  MajorDisplayStatus.LIVE_NEED_ATT, "creativegroup.displaystatus.live_linked_creatives_keywords_n_a"),
    DATE_NOT_IN_RANGE                        (5,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.date_not_in_range"),
    NO_AVAILABLE_BUDGET                      (6,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.no_available_budget"),
    NOT_LIVE_LINKED_CREATIVE_NEED_ATT        (7,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.not_live_linked_creatives_n_a"),
    NOT_LIVE_KW_NEED_ATT                     (8,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.not_live_keywords_n_a"),
    NOT_LIVE_LINKED_CREATIVES_AND_KW_NEED_ATT(9,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.not_live_linked_creatives_keywords_n_a"),
    NOT_LIVE_CHANNEL_TARGET_NEED_ATT         (10,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.not_live_channel_target_n_a"),
    DECLINED                                 (11,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.declined"),
    PENDING_FOROS                            (12,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.pending_foros"),
    PENDING_USER                             (13,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.pending_user"),
    INACTIVE                                 (14,  MajorDisplayStatus.INACTIVE, "creativegroup.displaystatus.inactive"),
    DELETED                                  (15,  MajorDisplayStatus.DELETED, "creativegroup.displaystatus.deleted"),
    GOAL_REACHED                             (16,  MajorDisplayStatus.NOT_LIVE, "creativegroup.displaystatus.goal_reached"),
    LIVE_CHANNEL_TARGET_NEED_ATT             (17,  MajorDisplayStatus.LIVE_NEED_ATT, "creativegroup.displaystatus.live_channel_target_n_a");

    private static Map<Integer, CcgDisplayStatus> displayStatuses = initDisplayStatusMap();

    private int id;
    private MajorDisplayStatus majorStatus;
    private String descriptionKey;

    CcgDisplayStatus(int id, MajorDisplayStatus majorStatus, String descriptionKey) {
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

    public static CcgDisplayStatus valueOf(Integer id) {
        return displayStatuses.get(id);
    }

    private static Map<Integer, CcgDisplayStatus> initDisplayStatusMap() {
        Map<Integer, CcgDisplayStatus> result = new HashMap<>(CcgDisplayStatus.values().length);
        for (CcgDisplayStatus status : CcgDisplayStatus.values()) {
            result.put(status.getId(), status);
        }
        return result;
    }
}
