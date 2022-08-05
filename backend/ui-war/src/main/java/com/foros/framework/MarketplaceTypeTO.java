package com.foros.framework;

import com.foros.model.account.MarketplaceType;

public class MarketplaceTypeTO {
    private boolean inFOROS;
    private boolean inWG;

    public MarketplaceTypeTO() {
    }

    public MarketplaceTypeTO(MarketplaceType marketplaceType) {
        populateValues(marketplaceType);
    }

    public boolean isInFOROS() {
        return inFOROS;
    }

    public void setInFOROS(boolean inFOROS) {
        this.inFOROS = inFOROS;
    }

    public boolean isInWG() {
        return inWG;
    }

    public void setInWG(boolean inWG) {
        this.inWG = inWG;
    }

    private void populateValues(MarketplaceType marketplaceType) {
        inFOROS = marketplaceType.isInFOROS();
        inWG = marketplaceType.isInWG();
    }

    public void setEnum(MarketplaceType marketplaceType) {
        populateValues(marketplaceType);
    }
    
    public MarketplaceType getEnum() {
        return MarketplaceType.byFlags(inFOROS, inWG);
    }
}
