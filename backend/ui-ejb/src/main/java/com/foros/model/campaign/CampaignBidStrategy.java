package com.foros.model.campaign;

public enum CampaignBidStrategy {
    MAXIMISE_REACH,
    CTR_BY_AMOUNT,
    CTR_BY_PREDICTION,
    MARGIN;

    public static void main(String[] args) throws ClassNotFoundException {
        Class clazz = Class.forName("com.foros.model.campaign.CampaignBidStrategy");
        System.out.println(Enum.valueOf(clazz, "CTR_BY_AMOUNT"));
    }
}
