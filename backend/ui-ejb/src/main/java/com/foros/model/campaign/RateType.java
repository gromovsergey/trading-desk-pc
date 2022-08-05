package com.foros.model.campaign;

public enum RateType {
    /** Cost Per Millennium (1000 impressions)  */
    CPM("CPM"),
    /** Cost Per Click **/
    CPC("CPC"),
    /** Cost Per Action **/
    CPA("CPA");
    
    private final String name;

    RateType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RateType valueOf(int ordinal) {
        for (RateType rateType : values()) {
            if (rateType.ordinal() == ordinal) {
                return rateType;
            }
        }

        throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
    }
}
