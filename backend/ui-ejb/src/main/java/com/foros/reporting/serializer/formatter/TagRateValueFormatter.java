package com.foros.reporting.serializer.formatter;

import java.math.BigDecimal;

import com.foros.reporting.meta.Column;
import com.foros.session.site.TagPricingUtil;
import com.foros.util.StringUtil;

public class TagRateValueFormatter extends ValueFormatterSupport<Number> {
    private final Column countryCodeColumn;
    private final Column currencyColumn;
    private final Column rateTypeColumn;
    private final Column ccgTypeColumn;
    private final Column siteRateTypeColumn;

    public TagRateValueFormatter(
            Column currencyColumn,
            Column countryCodeColumn,
            Column rateTypeColumn,
            Column ccgTypeColumn,
            Column siteRateTypeColumn) {
        this.currencyColumn = currencyColumn;
        this.countryCodeColumn = countryCodeColumn;
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

        String countryCode = (String) context.getRow().get(countryCodeColumn);
        sb.append(StringUtil.isPropertyNotEmpty(countryCode) ? countryCode : StringUtil.getLocalizedString("site.edittag.tagPricings.country.defaultWorldwide"));
        sb.append(": ");

        String siteRateTypeValue = (String) context.getRow().get(siteRateTypeColumn);
        TagPricingUtil.appendSiteRate(sb, siteRateTypeValue, value, context.getLocale(), currencyCode);

        String ccgTypeValue = (String) context.getRow().get(ccgTypeColumn);
        String rateTypeValue = (String) context.getRow().get(rateTypeColumn);
        TagPricingUtil.appendDescription(sb, ccgTypeValue, rateTypeValue);

        return sb.toString();
    }
}
