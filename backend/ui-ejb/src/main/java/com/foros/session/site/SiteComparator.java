package com.foros.session.site;

import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.PassbackType;
import com.foros.model.site.Site;
import com.foros.model.site.SiteRate;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.util.CollectionUtils;
import com.foros.util.EqualsUtil;
import com.foros.util.StringUtil;
import com.foros.util.mapper.Converter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alexey_chernenko
 */
public class SiteComparator {

    private static final class ProtocolNameConverter implements Converter<CreativeSize, String> {
        @Override
        public String item(CreativeSize value) {
            return value.getProtocolName();
        }
    }

    /**
     * Compares two instances of sites in terms of field equality.
     * Current method compares sites name and url only
     */
    public static boolean equals(Site existing, Site current) {
        if (existing == null || current == null) {
            return existing == current;
        }

        if (!equals(existing.getName(), current.getName()) ||
            !equals(existing.getSiteUrl(), current.getSiteUrl())) {
            return false;
        }

        return true;
    }

    /**
     * Compares two instances of tag objects, compares names, protocol name (creative size name),
     * passback, passback htmls and tag pricing values.
     */
    public static boolean equals(Tag existing, Tag current) {
        if (existing == null || current == null) {
            return existing == current;
        }

        //check if pricing count matches
        if(existing.getTagPricings().size() != current.getTagPricings().size()) {
            return false;
        }

        Collection<String> existingProtocolNames = CollectionUtils.convert(new ProtocolNameConverter(), existing.getSizes());
        Collection<String> currentProtocolNames = CollectionUtils.convert(new ProtocolNameConverter(), current.getSizes());
        if (!equals(existing.getName(), current.getName()) ||
                !existingProtocolNames.equals(currentProtocolNames)) {
            return false;
        }

        if (!existing.getSizeType().getDefaultName().equals(current.getSizeType().getDefaultName())) {
            return false;
        }

        if (!existing.getFlags().equals(current.getFlags())) {
            return false;
        }

        if (current.getPassbackType() != existing.getPassbackType()) {
            return false;
        }

        // Some messing with passbacks, if passback is of HTML_URL type, then existing and current values
        // must be the same
        if (existing.getPassbackType() == PassbackType.HTML_URL) {
            if (!equals(existing.getPassback(), current.getPassback())) {
                return false;
            }
        } else { // If its not a Url, then it refferes to html file which content is set to passbackHtml field. So compare htmls.
            if (!equals(existing.getPassbackHtml(), current.getPassbackHtml())) {
                return false;
            }
        }

        Map<Country, TagPricing> pricingByCountry = convertPricingToMap(existing.getTagPricings());
        for (TagPricing pricing : current.getTagPricings()) {
            TagPricing existingPricing = pricingByCountry.get(pricing.getCountry());
            if (!equals(existingPricing, pricing)) return false;
        }
        return true;
    }

    public static boolean equals(TagPricing existing, TagPricing current) {
        if (existing == null || current == null) {
            return existing == current;
        }
        return equals(existing.getSiteRate(), current.getSiteRate());
    }

    public static boolean equals(SiteRate existing, SiteRate current) {
        if (existing == null || current == null) {
            return existing == current;
        }
        return EqualsUtil.equalsBigDecimal(existing.getRate(), current.getRate())
                && existing.getRateType()==current.getRateType();
    }

    public static Map<Long, Tag> convertTagsToMap(Collection<Tag> tags) {
        Map<Long, Tag> tagsMap = new HashMap<Long, Tag>();
        if (tags != null && !tags.isEmpty()) {
            for (Tag tag : tags) {
                if (tag.getStatus() == Status.ACTIVE) {
                    tagsMap.put(tag.getId(), tag);
                }
            }
        }
        return tagsMap;
    }

    private static Map<Country, TagPricing> convertPricingToMap(Collection<TagPricing> tagPricings) {
        Map<Country, TagPricing> pricingByCountry = new HashMap<Country, TagPricing>();
        for (TagPricing pricing : tagPricings) {
            if (pricing.getStatus() == Status.ACTIVE) {
                pricingByCountry.put(pricing.getCountry(), pricing);
            }
        }
        return pricingByCountry;
    }

    private static boolean equals(String o1, String o2) {
        return StringUtil.toString(o1).equals(StringUtil.toString(o2));
    }
}
