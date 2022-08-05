package app.programmatic.ui.flight.view;

import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.common.view.TimeSpan;
import app.programmatic.ui.flight.dao.model.FrequencyCap;

public class FrequencyCapView {
    private Long id;
    private TimeSpan period;
    private TimeSpan windowLength;
    private Integer windowCount;
    private Integer lifeCount;
    private Long version;

    public FrequencyCapView() {
    }

    FrequencyCapView(FrequencyCap frequencyCap) {
        id = frequencyCap.getId();

        period = new TimeSpan();
        period.setValueInSeconds(frequencyCap.getPeriod());

        windowLength = new TimeSpan();
        windowLength.setValueInSeconds(frequencyCap.getWindowLength());

        windowCount = frequencyCap.getWindowCount();
        lifeCount = frequencyCap.getLifeCount();
        version = XmlDateTimeConverter.convertToEpochTime(frequencyCap.getVersion());
    }

    FrequencyCap buildFrequencyCap() {
        FrequencyCap result = new FrequencyCap();

        result.setId(id);
        result.setPeriod(period != null ? period.getValueInSeconds() : null);
        result.setWindowLength(windowLength != null ? windowLength.getValueInSeconds() : null);
        result.setWindowCount(windowCount);
        result.setLifeCount(lifeCount);
        result.setVersion(XmlDateTimeConverter.convertEpochToTimestamp(version));

        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TimeSpan getPeriod() {
        return period;
    }

    public void setPeriod(TimeSpan period) {
        this.period = period;
    }

    public TimeSpan getWindowLength() {
        return windowLength;
    }

    public void setWindowLength(TimeSpan windowLength) {
        this.windowLength = windowLength;
    }

    public Integer getWindowCount() {
        return windowCount;
    }

    public void setWindowCount(Integer windowCount) {
        this.windowCount = windowCount;
    }

    public Integer getLifeCount() {
        return lifeCount;
    }

    public void setLifeCount(Integer lifeCount) {
        this.lifeCount = lifeCount;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
