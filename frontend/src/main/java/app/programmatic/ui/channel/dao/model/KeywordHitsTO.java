package app.programmatic.ui.channel.dao.model;

import java.math.BigDecimal;

public class KeywordHitsTO {
    private String keyword;
    private String type;
    private Long hits;
    private Long imps;
    private Long clicks;
    private BigDecimal ctr;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
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

    public BigDecimal getCtr() {
        return ctr;
    }

    public void setCtr(BigDecimal ctr) {
        this.ctr = ctr;
    }
}
