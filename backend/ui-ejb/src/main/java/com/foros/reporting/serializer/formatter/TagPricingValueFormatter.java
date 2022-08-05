package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;
import com.foros.session.site.TagPricingUtil;
import com.foros.util.StringUtil;

import java.math.BigDecimal;

public class TagPricingValueFormatter extends ValueFormatterSupport<BigDecimal> {
    private Column countryCodeColumn;
    private String currencyCode;

    private Column rateTypeColumn;

    private Column ccgTypeColumn;

    private Column siteRateTypeColumn;

    public TagPricingValueFormatter(Column countryCodeColumn, String currencyCode, Column rateTypeColumn, Column ccgTypeColumn, Column siteRateTypeColumn) {
        this.countryCodeColumn = countryCodeColumn;
        this.currencyCode = currencyCode;
        this.rateTypeColumn = rateTypeColumn;
        this.ccgTypeColumn = ccgTypeColumn;
        this.siteRateTypeColumn = siteRateTypeColumn;
    }

    @Override
    public String formatText(BigDecimal value, FormatterContext context) {
        StringBuilder buf = new StringBuilder();

        String countryCode = (String) context.getRow().get(countryCodeColumn);
        buf.append(StringUtil.isPropertyNotEmpty(countryCode) ? countryCode : StringUtil.getLocalizedString("site.edittag.tagPricings.country.defaultWorldwide"));
        buf.append(": ");

        String tagRateType = (String) context.getRow().get(siteRateTypeColumn);
        TagPricingUtil.appendSiteRate(buf, tagRateType, value, context.getLocale(), currencyCode);

        String ccgTypeValue = (String) context.getRow().get(ccgTypeColumn);
        String rateTypeValue = (String) context.getRow().get(rateTypeColumn);
        TagPricingUtil.appendDescription(buf, ccgTypeValue, rateTypeValue);
        return buf.toString();

    }


}
