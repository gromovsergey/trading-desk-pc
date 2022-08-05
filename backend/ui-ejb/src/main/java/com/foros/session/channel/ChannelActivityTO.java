package com.foros.session.channel;

import java.math.BigDecimal;
import java.util.Date;

public class ChannelActivityTO {
    private Date statsDate;
    private BigDecimal pageKeywords;
    private BigDecimal pageKeywordsPc;
    private BigDecimal searchKeywords;
    private BigDecimal searchKeywordsPc;
    private BigDecimal urls;
    private BigDecimal urlsPc;
    private BigDecimal urlKeywords;
    private BigDecimal urlKeywordsPc;
    private BigDecimal totalHits;
    private BigDecimal totalUniques;
    private BigDecimal activeDailyUniques;
    private BigDecimal impressions;
    private BigDecimal clicks;
    private BigDecimal ctr;
    private BigDecimal ecpm;
    private BigDecimal value;

    public Date getStatsDate() {
        return statsDate;
    }

    public void setStatsDate(Date statsDate) {
        this.statsDate = statsDate;
    }

    public BigDecimal getPageKeywords() {
        return pageKeywords;
    }

    public void setPageKeywords(BigDecimal pageKeywords) {
        this.pageKeywords = pageKeywords;
    }

    public BigDecimal getPageKeywordsPercent() {
        return pageKeywordsPc;
    }

    public void setPageKeywordsPercent(BigDecimal pageKeywordsPc) {
        this.pageKeywordsPc = pageKeywordsPc;
    }

    public BigDecimal getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(BigDecimal searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public BigDecimal getSearchKeywordsPercent() {
        return searchKeywordsPc;
    }

    public void setSearchKeywordsPercent(BigDecimal searchKeywordsPc) {
        this.searchKeywordsPc = searchKeywordsPc;
    }

    public BigDecimal getUrls() {
        return urls;
    }

    public void setUrls(BigDecimal urls) {
        this.urls = urls;
    }

    public BigDecimal getUrlsPercent() {
        return urlsPc;
    }

    public void setUrlsPercent(BigDecimal urlsPc) {
        this.urlsPc = urlsPc;
    }

    public BigDecimal getUrlKeywords() {
        return urlKeywords;
    }

    public void setUrlKeywords(BigDecimal urlKeywords) {
        this.urlKeywords = urlKeywords;
    }

    public BigDecimal getUrlKeywordsPercent() {
        return urlKeywordsPc;
    }

    public void setUrlKeywordsPercent(BigDecimal urlKeywordsPc) {
        this.urlKeywordsPc = urlKeywordsPc;
    }

    public BigDecimal getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(BigDecimal totalHits) {
        this.totalHits = totalHits;
    }

    public BigDecimal getTotalUniques() {
        return totalUniques;
    }

    public void setTotalUniques(BigDecimal totalUniques) {
        this.totalUniques = totalUniques;
    }

    public BigDecimal getActiveDailyUniques() {
        return activeDailyUniques;
    }

    public void setActiveDailyUniques(BigDecimal activeDailyUniques) {
        this.activeDailyUniques = activeDailyUniques;
    }

    public BigDecimal getImpressions() {
        return impressions;
    }

    public void setImpressions(BigDecimal impressions) {
        this.impressions = impressions;
    }

    public BigDecimal getEcpm() {
        return ecpm;
    }

    public void setEcpm(BigDecimal ecpm) {
        this.ecpm = ecpm;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public void setCtr(BigDecimal ctr) {
        this.ctr = ctr;
    }

    public BigDecimal getClicks() {
        return clicks;
    }

    public void setClicks(BigDecimal clicks) {
        this.clicks = clicks;
    }
}
