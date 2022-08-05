package app.programmatic.ui.channel.dao.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import app.programmatic.ui.common.tool.serialization.JsonDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class KeywordTypeHitsTO {
    private LocalDateTime date;
    private KeywordHits pageKeywordHits;
    private KeywordHits searchKeywordHits;
    private KeywordHits urlHits;
    private KeywordHits urlKeywordHits;
    private Long totalHits;
    private Long totalUniques;
    private Long activeDailyUniques;
    private Long imps;
    private Long clicks;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public KeywordHits getPageKeywordHits() {
        return pageKeywordHits;
    }

    public void setPageKeywordHits(KeywordHits pageKeywordHits) {
        this.pageKeywordHits = pageKeywordHits;
    }

    public KeywordHits getSearchKeywordHits() {
        return searchKeywordHits;
    }

    public void setSearchKeywordHits(KeywordHits searchKeywordHits) {
        this.searchKeywordHits = searchKeywordHits;
    }

    public KeywordHits getUrlHits() {
        return urlHits;
    }

    public void setUrlHits(KeywordHits urlHits) {
        this.urlHits = urlHits;
    }

    public KeywordHits getUrlKeywordHits() {
        return urlKeywordHits;
    }

    public void setUrlKeywordHits(KeywordHits urlKeywordHits) {
        this.urlKeywordHits = urlKeywordHits;
    }

    public Long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Long totalHits) {
        this.totalHits = totalHits;
    }

    public Long getTotalUniques() {
        return totalUniques;
    }

    public void setTotalUniques(Long totalUniques) {
        this.totalUniques = totalUniques;
    }

    public Long getActiveDailyUniques() {
        return activeDailyUniques;
    }

    public void setActiveDailyUniques(Long activeDailyUniques) {
        this.activeDailyUniques = activeDailyUniques;
    }

    public Long getImps() {
        return imps;
    }

    public void setImps(Long imps) {
        this.imps = imps;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public static class KeywordHits {
        private final Long hits;
        private final BigDecimal percent;

        public KeywordHits(Long hits, BigDecimal percent) {
            this.hits = hits;
            this.percent = percent;
        }

        public Long getHits() {
            return hits;
        }

        public BigDecimal getPercent() {
            return percent;
        }
    }
}
