package com.foros.action.campaign.bulk;

import com.foros.model.EntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.reporting.RowType;

public enum CampaignLevelCsv {

    Campaign("Campaign", new RowType("campaign")),
    AdGroup("Ad Group", new RowType("adGroup")),
    TextAd("Text Ad", new RowType("textAd")),
    Keyword("Keyword", new RowType("keyword"));

    private String name;
    private RowType rowType;

    private CampaignLevelCsv(String name, RowType rowType) {
        this.name = name;
        this.rowType = rowType;
    }

    public String getName() {
        return name;
    }

    public RowType getRowType() {
        return rowType;
    }

    public static CampaignLevelCsv byEntity(EntityBase entity) {
        if (entity instanceof Campaign) {
            return Campaign;
        } else if (entity instanceof CampaignCreativeGroup) {
            return AdGroup;
        } else if (entity instanceof CCGKeyword) {
            return Keyword;
        } else if (entity instanceof CampaignCreative) {
            return TextAd;
        } else if (entity instanceof Creative) {
            return TextAd;
        } else {
            throw new IllegalArgumentException(entity.getClass().getSimpleName());
        }
    }
}
