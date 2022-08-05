package com.foros.model.campaign;

public enum CampaignCreditPurpose {
    C, // Compensation
    I; // Incentive

    public static CampaignCreditPurpose valueOf(char letter) throws IllegalArgumentException {
        return valueOf(String.valueOf(letter));
    }
}