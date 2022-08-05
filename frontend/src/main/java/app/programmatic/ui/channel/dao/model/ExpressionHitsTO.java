package app.programmatic.ui.channel.dao.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import app.programmatic.ui.common.tool.serialization.JsonDateTimeSerializer;

import java.time.LocalDateTime;

public class ExpressionHitsTO {
    private LocalDateTime date;
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
}
