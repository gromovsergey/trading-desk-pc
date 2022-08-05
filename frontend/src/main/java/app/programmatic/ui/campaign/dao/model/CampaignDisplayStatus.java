package app.programmatic.ui.campaign.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.HashMap;
import java.util.Map;


public enum CampaignDisplayStatus {
    LIVE(1, MajorDisplayStatus.LIVE, "campaign.displaystatus.live"),
    LIVE_NEED_ATT(2, MajorDisplayStatus.LIVE_NEED_ATT, "campaign.displaystatus.live_na"),
    NOT_LIVE_NEED_ATT(3, MajorDisplayStatus.NOT_LIVE, "campaign.displaystatus.not_live_na"),
    NO_AVAIL_BUDGET(4, MajorDisplayStatus.NOT_LIVE, "campaign.displaystatus.no_avail_budget"),
    NO_ACTIVE_GROUPS(5, MajorDisplayStatus.INACTIVE, "campaign.displaystatus.no_active_groups"),
    DELETED(6, MajorDisplayStatus.DELETED, "campaign.displaystatus.deleted"),
    INACTIVE(7, MajorDisplayStatus.INACTIVE, "campaign.displaystatus.inactive"),
    DATE_NOT_IN_RANGE(8, MajorDisplayStatus.NOT_LIVE, "campaign.displaystatus.date_not_in_range");

    private static Map<Integer, CampaignDisplayStatus> displayStatuses = initDisplayStatusMap();

    private int id;
    private MajorDisplayStatus majorStatus;
    private String descriptionKey;

    CampaignDisplayStatus(int id, MajorDisplayStatus majorStatus, String descriptionKey) {
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

    public static CampaignDisplayStatus valueOf(Integer id) {
        return displayStatuses.get(id);
    }

    private static Map<Integer, CampaignDisplayStatus> initDisplayStatusMap() {
        Map<Integer, CampaignDisplayStatus> result = new HashMap<>(CampaignDisplayStatus.values().length);
        for (CampaignDisplayStatus status : CampaignDisplayStatus.values()) {
            result.put(status.getId(), status);
        }
        return result;
    }
}
