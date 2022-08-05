package com.foros.model.campaign;

import com.foros.session.DisplayStatusEntityTO;
import com.foros.session.security.AccountTO;

import java.io.Serializable;

public class CampaignAssociationTO implements Serializable {
    private final AccountTO account;
    private final AccountTO advertiser;
    private final DisplayStatusEntityTO campaign;
    private final DisplayStatusEntityTO group;
    private Long clicks;
    private Long impressions;
    private Double ctr;

    public AccountTO getAccount() {
        return account;
    }

    public AccountTO getAdvertiser() {
        return advertiser;
    }

    public DisplayStatusEntityTO getCampaign() {
        return campaign;
    }

    public DisplayStatusEntityTO getGroup() {
        return group;
    }

    public Long getClicks() {
        return clicks;
    }

    public Long getImpressions() {
        return impressions;
    }

    public Double getCtr() {
        return ctr;
    }

    public static class Builder {
        private AccountTO account;
        private AccountTO advertiser;
        private DisplayStatusEntityTO campaign;
        private DisplayStatusEntityTO group;
        private Long clicks;
        private Long impressions;
        private Double ctr;

        public Builder account(AccountTO account) {
            this.account = account;
            return this;
        }

        public Builder advertiser(AccountTO advertiser) {
            this.advertiser = advertiser;
            return this;
        }

        public Builder campaign(DisplayStatusEntityTO campaign) {
            this.campaign = campaign;
            return this;
        }

        public Builder group(DisplayStatusEntityTO group) {
            this.group = group;
            return this;
        }

        public Builder clicks(Long clicks) {
            this.clicks = clicks;
            return this;
        }

        public Builder impressions(Long impressions) {
            this.impressions = impressions;
            return this;
        }

        public Builder ctr(Double ctr) {
            this.ctr = ctr;
            return this;
        }

        public CampaignAssociationTO build() {
            return new CampaignAssociationTO(this);
        }
    }

    private CampaignAssociationTO(Builder builder) {
        this.account = builder.account;
        this.advertiser = builder.advertiser;
        this.campaign = builder.campaign;
        this.group = builder.group;
        this.clicks = builder.clicks;
        this.impressions = builder.impressions;
        this.ctr = builder.ctr;
    }
}
