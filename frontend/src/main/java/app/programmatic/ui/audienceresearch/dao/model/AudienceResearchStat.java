package app.programmatic.ui.audienceresearch.dao.model;

import java.math.BigDecimal;
import java.util.List;

public class AudienceResearchStat {
    private String channelName;

    private List<String> dates;
    private List<String> ticks;
    private List<List<BigDecimal>> values;

    private String lastDate;
    private List<BigDecimal> lastValues;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<String> getTicks() {
        return ticks;
    }

    public void setTicks(List<String> ticks) {
        this.ticks = ticks;
    }

    public List<List<BigDecimal>> getValues() {
        return values;
    }

    public void setValues(List<List<BigDecimal>> values) {
        this.values = values;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public List<BigDecimal> getLastValues() {
        return lastValues;
    }

    public void setLastValues(List<BigDecimal> lastValues) {
        this.lastValues = lastValues;
    }
}
