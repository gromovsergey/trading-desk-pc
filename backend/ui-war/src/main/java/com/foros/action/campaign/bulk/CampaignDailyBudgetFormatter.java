package com.foros.action.campaign.bulk;

import com.foros.model.campaign.DeliveryPacing;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;

public class CampaignDailyBudgetFormatter extends ValueFormatterSupport<Object> {
    private ValueFormatter currencyFormatter;

    public CampaignDailyBudgetFormatter(ValueFormatter currencyFormatter) {
        this.currencyFormatter = currencyFormatter;
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        if (value instanceof DeliveryPacing) {
            return ((DeliveryPacing) value).getName();
        } else {
            return currencyFormatter.formatText(value, context);
        }
    }
}
