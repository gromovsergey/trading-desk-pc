package app.programmatic.ui.site.dao.model;

import java.math.BigDecimal;

public class SiteStat extends Site {
    private Long imps;
    private Long clicks;
    private BigDecimal ctr;

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
