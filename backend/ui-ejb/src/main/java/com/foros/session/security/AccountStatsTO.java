package com.foros.session.security;

import com.foros.model.DisplayStatus;
import com.foros.model.IdNameEntity;
import com.foros.model.account.Account;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: paresh.morker
 * Date: Nov 28, 2008
 * Time: 3:11:16 PM
 */
public class AccountStatsTO implements IdNameEntity, Serializable {
    private Long id;
    private String name;
    private String currencyCode;
    private Long creditedImpressions;
    private Long impressions;
    private Long clicks;
    private BigDecimal ctr;
    private Long requests;
    private Long users;
    private BigDecimal cost;
    private BigDecimal revenue;
    private DisplayStatus displayStatus;
    private boolean testFlag;
    private BigDecimal creditUsed;

    public static AccountStatsTOBuilder builder() {
        AccountStatsTO instance = new AccountStatsTO();
        return instance.new AccountStatsTOBuilder();
    }

    public class AccountStatsTOBuilder {
        private AccountStatsTO instance = AccountStatsTO.this;

        public AccountStatsTOBuilder withId(Long id) {
            instance.id = id;
            return this;
        }

        public AccountStatsTOBuilder withName(String name) {
            instance.name = name;
            return this;
        }

        public AccountStatsTOBuilder withCurrencyCode(String code) {
            instance.currencyCode = code;
            return this;
        }

        public AccountStatsTOBuilder withImps(Long imps) {
            instance.impressions = imps;
            return this;
        }

        public AccountStatsTOBuilder withCreditedImps(Long imps) {
            instance.creditedImpressions = imps;
            return this;
        }

        public AccountStatsTOBuilder withRequests(Long requests) {
            instance.requests = requests;
            return this;
        }

        public AccountStatsTOBuilder withCost(BigDecimal cost) {
            instance.cost = cost;
            return this;
        }

        public AccountStatsTOBuilder withUsers(Long users) {
            instance.users = users;
            return this;
        }

        public AccountStatsTOBuilder withClicks(Long clicks) {
            instance.clicks = clicks;
            return this;
        }

        public AccountStatsTOBuilder withCtr(BigDecimal ctr) {
            instance.ctr = ctr;
            return this;
        }

        public AccountStatsTOBuilder withAdvAmount(BigDecimal adv) {
            instance.revenue = adv;
            return this;
        }

        public AccountStatsTOBuilder withDisplayStatusId(Long id) {
            instance.displayStatus = Account.getDisplayStatus(id);
            return this;
        }

        public AccountStatsTOBuilder withIsTest(boolean isTest) {
            instance.testFlag = isTest;
            return this;
        }

        public AccountStatsTOBuilder withUsedAmount(BigDecimal creditUsed) {
            instance.creditUsed = creditUsed;
            return this;
        }

        public AccountStatsTO build() {
            return instance;
        }
    }

    // Generic constructor
    private AccountStatsTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Long getCreditedImpressions() {
        return creditedImpressions;
    }

    public Long getImpressions() {
        return impressions;
    }

    public Long getClicks() {
        return clicks;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public Long getRequests() {
        return requests;
    }

    public Long getUsers() {
        return users;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public boolean isTestFlag() {
        return testFlag;
    }

    public BigDecimal getCreditUsed() {
        return creditUsed;
    }
}
