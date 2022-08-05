package com.foros.session.site;

import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.TagPricing;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.PercentValueFormatter;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;

import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;

public class TagPricingUtil {

    public static boolean isSameTagPricing(TagPricing tp1, TagPricing tp2) {
        return new EqualsBuilder()
                .append(tp2.getCcgType(), tp1.getCcgType())
                .append(tp2.getCcgRateType(), tp1.getCcgRateType())
                .append(tp2.getCountry(), tp1.getCountry())
                .isEquals();
    }

    public static void appendDescription(StringBuilder sb, String ccgTypeValue, String rateTypeValue) {
        if (ccgTypeValue != null) {
            sb.append(" ");
            sb.append(StringUtil.getLocalizedString("ccg.type." + CCGType.valueOfString(ccgTypeValue).getPageExtension()));
        }
        if (rateTypeValue != null) {
            sb.append(" ");
            sb.append(StringUtil.getLocalizedString("enum.RateType." + RateType.valueOf(rateTypeValue).name()));
        }
    }

    public static void appendSiteRate(StringBuilder builder, String siteRateTypeValue, Number value, Locale locale, String currencyCode) {
        SiteRateType siteRateType;
        if (siteRateTypeValue != null) {
            siteRateType = SiteRateType.valueOf(siteRateTypeValue.trim());
        } else {
            siteRateType = SiteRateType.CPM;
        }

        FormatterContext context = new FormatterContext(locale);
        switch (siteRateType) {
        case RS:
            builder.append(new PercentValueFormatter().formatText(NumberUtil.toPercents(NumberUtil.toBigDecimal(value)), context));
            break;
        default:
            CurrencyValueFormatter formatter = new CurrencyValueFormatter(currencyCode);
            builder.append(formatter.formatText(NumberUtil.toBigDecimal(value), context));
            break;
        }

        builder.append(' ').append(StringUtil.getLocalizedString("enums.SiteRateType." + siteRateType.name()));
    }

}
