package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;
import com.foros.session.site.TagPricingUtil;

import java.math.BigDecimal;

public class SiteRateValueFormatter extends ValueFormatterSupport<Number> {

    private Column currencyColumn;

    private Column rateTypeColumn;

    private Column ccgTypeColumn;

    private Column siteRateTypeColumn;

    public SiteRateValueFormatter(Column currencyColumn, Column rateTypeColumn, Column ccgTypeColumn, Column siteRateTypeColumn) {
        this.currencyColumn = currencyColumn;
        this.rateTypeColumn = rateTypeColumn;
        this.ccgTypeColumn = ccgTypeColumn;
        this.siteRateTypeColumn = siteRateTypeColumn;
    }

    @Override
    public String formatText(Number value, FormatterContext context) {
        String currencyCode = (String) context.getRow().get(currencyColumn);

        if (value == null && currencyCode == null) {
            return "";
        }

        if (value == null) {
            value = BigDecimal.ZERO;
        }

        StringBuilder sb = new StringBuilder();
        String siteRateTypeValue = (String) context.getRow().get(siteRateTypeColumn);
        TagPricingUtil.appendSiteRate(sb, siteRateTypeValue, value, context.getLocale(), currencyCode);

        String ccgTypeValue = (String) context.getRow().get(ccgTypeColumn);
        String rateTypeValue = (String) context.getRow().get(rateTypeColumn);
        TagPricingUtil.appendDescription(sb, ccgTypeValue, rateTypeValue);

        return sb.toString();
    }
}
