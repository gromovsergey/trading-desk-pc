package com.foros.model.action;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ConversionCategory {
    OTHER("Action.other", 6),
    PURCHASE("Action.purchase", 0),
    ADD_TO_CART("Action.addToCart", 1),
    KEY_PAGE_VIEW("Action.keyPageView", 2),
    SIGNUP("Action.signup", 4),
    LEAD("Action.lead", 5),
    LANDING_PAGE_VIEW("Action.landingPageView", 3);

    private final String nameKey;
    private final int order;

    ConversionCategory(String nameKey, int order) {
        this.nameKey = nameKey;
        this.order = order;
    }

    public int getId() {
        return ordinal();
    }

    public String getNameKey() {
        return nameKey;
    }

    public static ConversionCategory valueOf(int i) {
        if (i < 0 || i >= ConversionCategory.values().length) {
            throw new IllegalArgumentException("Invalid ordinal");
        }
        return ConversionCategory.values()[i];
    }

    public static ConversionCategory[] sorted() {
        ConversionCategory sorted[] = new ConversionCategory[values().length];
        for (ConversionCategory conversionCategory : values()) {
            sorted[conversionCategory.order] = conversionCategory;
        }
        return sorted;
    }
}
