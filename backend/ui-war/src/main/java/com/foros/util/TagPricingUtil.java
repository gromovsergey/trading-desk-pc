package com.foros.util;

import com.foros.model.site.SiteRate;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import java.util.Locale;

public class TagPricingUtil {
    public static String formatTagPricings(Tag tag) throws java.text.ParseException {
        StringBuilder buf = new StringBuilder();
        String currencyCode = tag.getSite().getAccount().getCurrency().getCurrencyCode();
        boolean isFirstTagPricing = true;
        SiteRate defSiteRate = null;
        Locale locale = CurrentUserSettingsHolder.getLocale();

        for (TagPricing pricing : tag.getTagPricings()) {
            if (defSiteRate == null && pricing.getCountry() == null) {
                defSiteRate = pricing.getSiteRate();
                continue;
            }

            if (!isFirstTagPricing) {
                buf.append("; ");
            }

            if (pricing.getCountry() == null) {
                buf.append(StringUtil.getLocalizedString("site.edittag.tagPricings.country.defaultWorldwide"));
            } else {
                buf.append(pricing.getCountry().getCountryCode());
            }
            buf.append(": ");

            com.foros.session.site.TagPricingUtil.appendSiteRate(buf, pricing.getSiteRate().getRateType().name(), pricing.getSiteRate().getRate(), locale, currencyCode);
            buf.append(" ");
            String rateTypeValue = pricing.getCcgRateType() != null ? pricing.getCcgRateType().name() : null;
            String ccgTypeValue = pricing.getCcgType() != null ? String.valueOf(pricing.getCcgType().getLetter()) : null;
            com.foros.session.site.TagPricingUtil.appendDescription(buf, ccgTypeValue, rateTypeValue);
            isFirstTagPricing = false;
        }

        if (defSiteRate != null && defSiteRate.getRate() != null) {

            if (!isFirstTagPricing) {
                buf.append("; ");
            }
            buf.append(StringUtil.getLocalizedString("site.edittag.tagPricings.country.defaultWorldwide"));
            buf.append(": ");
            com.foros.session.site.TagPricingUtil.appendSiteRate(buf, defSiteRate.getRateType().name(), defSiteRate.getRate(), locale, currencyCode);
        }

        return buf.toString();
    }
}
