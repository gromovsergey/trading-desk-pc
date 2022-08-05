package com.foros.action.campaign.bulk;

import com.foros.util.StringUtil;
import com.foros.util.csv.BaseBulkHelper;

import java.io.Serializable;

public class CampaignBulkHelper extends BaseBulkHelper implements Serializable {
    private static String campaignNotSetPhrase = "Not Set";
    private static String groupNotSetPhrase = "Linked to Campaign";

    public static String getNotSetPhrase(CampaignFieldCsv field) {
        String notSetPhrase;
        switch (field) {
            case CampaignEndDate:
                notSetPhrase = campaignNotSetPhrase;
                break;
            case AdGroupEndDate:
                notSetPhrase = groupNotSetPhrase;
                break;
            default:
                notSetPhrase = null;
        }
        return notSetPhrase;
    }

    public static CampaignLevelCsv parseLevel(String string) {
        for (CampaignLevelCsv level : CampaignLevelCsv.values()) {
            if (StringUtil.compareToIgnoreCase(string, level.getName()) == 0) {
                return level;
            }
        }
        return null;
    }
}