package com.foros.action.campaign.bulk;

import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;

public class LevelValueFormatter extends ValueFormatterSupport<CampaignLevelCsv> {
    @Override
    public String formatText(CampaignLevelCsv value, FormatterContext context) {
        return value == null ? null : value.getName();
    }
}