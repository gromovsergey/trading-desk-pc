package app.programmatic.ui.flight.dao.model;

import app.programmatic.ui.common.model.VersionEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;


@Entity
@Table(name = "FREQCAP")
public class FrequencyCap extends VersionEntityBase<Long> {

    @SequenceGenerator(name = "FreqCapGen", sequenceName = "FREQCAP_FREQ_CAP_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FreqCapGen")
    @Column(name = "FREQ_CAP_ID", nullable = false)
    private Long id;

    @Min(1)
    @Column(name = "period")
    private Long period;

    @Min(1)
    @Column(name = "window_length")
    private Long windowLength;

    @Min(1)
    @Column(name = "window_count")
    private Integer windowCount;

    @Min(1)
    @Column(name = "life_count")
    private Integer lifeCount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Long getWindowLength() {
        return windowLength;
    }

    public void setWindowLength(Long windowLength) {
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

    public void copyBusinessProperties(FrequencyCap from) {
        if (from == null) {
            return;
        }

        setLifeCount(from.getLifeCount());
        setPeriod(from.getPeriod());
        setWindowCount(from.getWindowCount());
        setWindowLength(from.getWindowLength());
    }

    public static FrequencyCap cloneBusinessProperties(FrequencyCap from) {
        if (from == null) {
            return null;
        }

        FrequencyCap result = new FrequencyCap();
        result.copyBusinessProperties(from);
        return result;
    }
}
