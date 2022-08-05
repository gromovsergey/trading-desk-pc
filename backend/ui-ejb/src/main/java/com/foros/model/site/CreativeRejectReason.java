package com.foros.model.site;

public enum CreativeRejectReason {
    CREATIVE_IS_BLANK(1, "Creative is blank or doesn't display properly"),
    CREATIVE_BREAKS_SITE(2, "Creative breaks the site layout"),
    CREATIVE_DOESNT_CLICK_THROUGH(3, "Creative doesn't click through to destination"),
    CREATIVE_HAS_INVALID_CATEGORY(4, "Creative has invalid category"),
    CREATIVE_HAS_INAPPROPRIATE_CONTENT(5, "Creative has offensive or inappropriate content"),
    DESTINATION_HAS_INAPPROPRIATE_CONTENT(6, "Destination has offensive or inappropriate content"),
    CREATIVE_HAS_COMPETITIVE_AD(7, "Creative has competitive advertising"),
    CREATIVE_IS_ALREADY_SERVED(8, "Creative is already served through a direct deal"),
    CREATIVE_THIRD_PARTY_AUDIT_FEEDBACK(9, "Audit Feedback"),

    OTHER(0, "Other");

    private final int id;
    private final String defaultName;

    CreativeRejectReason(int id, String defaultName) {
        this.id = id;
        this.defaultName = defaultName;
    }

    public int getId() {
        return id;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public static CreativeRejectReason valueOf(int id) {
        for (CreativeRejectReason reason : CreativeRejectReason.values()) {
            if (reason.getId() == id) {
                return reason;
            }
        }
        return null;
    }
}
