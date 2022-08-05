package com.foros.model.account;

public enum MarketplaceType {
    NOT_SET(false, false),
    WG(false, true),
    FOROS(true, false),
    ALL(true, true);

    private boolean inFOROS;
    private boolean inWG;

    MarketplaceType(boolean inFOROS, boolean inWG) {
        this.inFOROS = inFOROS;
        this.inWG = inWG;
    }

    public boolean isInFOROS() {
        return inFOROS;
    }

    public boolean isInWG() {
        return inWG;
    }

    public static MarketplaceType byFlags(boolean inFOROS, boolean inWG) {
        for (MarketplaceType type : values()) {
            if (type.inFOROS == inFOROS && type.inWG == inWG) {
                return type;
            }
        }
        throw new IllegalArgumentException("Actually this exception can't be thrown");
    }
}
