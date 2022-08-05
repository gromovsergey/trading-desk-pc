package com.foros.util.xml;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "channelMinUrlTriggerThreshold")
public class QADescriptionChannelMinUrlTriggerThreshold implements QADescription {

    private Long threshold;
    private Long value;

    public QADescriptionChannelMinUrlTriggerThreshold() {
    }

    public QADescriptionChannelMinUrlTriggerThreshold(Long threshold, Long value) {
        this.threshold = threshold;
        this.value = value;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "value=" + value + " threshold=" + threshold;
    }
}
