package com.foros.action.site.csv;

import com.foros.model.site.SiteRate;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.TagPricing;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collection;

public class TagPricingsFormatter extends ValueFormatterSupport<Collection<TagPricing>> {

    @Override
    public String formatText(Collection<TagPricing> tagPricings, FormatterContext context) {
        NumberFormat numberFormat = NumberFormat.getInstance(context.getLocale());

        String defaultPricing = "";
        StringBuilder buffer = new StringBuilder();
        for (TagPricing pricing : tagPricings) {
            SiteRate siteRate = pricing.getSiteRate();
            SiteRateType siteRateType = siteRate.getRateType();
            BigDecimal rateNumber = SiteRateType.RS == siteRateType ? siteRate.getRatePercent() : siteRate.getRate();
            if (pricing.isDefault()) {
                defaultPricing = numberFormat.format(rateNumber) + siteRateType;
            } else {
                buffer.append(";cc=")
                        .append(pricing.getCountry() == null ? "all" : pricing.getCountry().getCountryCode())
                        .append(",ct=")
                        .append(pricing.getCcgType() == null ? "all" : pricing.getCcgType().name().toLowerCase())
                        .append(",rt=")
                        .append(pricing.getCcgRateType() == null ? "all" : pricing.getCcgRateType().name()
                                .toLowerCase()).append(",p=")
                    .append(numberFormat.format(rateNumber))
                    .append(siteRateType);
            }
        }
        buffer.insert(0, defaultPricing.isEmpty() ? "" : "default=" + defaultPricing);
        return buffer.toString();
    }
}
