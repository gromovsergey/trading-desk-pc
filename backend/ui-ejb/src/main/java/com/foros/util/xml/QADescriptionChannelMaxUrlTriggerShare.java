package com.foros.util.xml;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "channelMaxUrlTriggerShare")
public class QADescriptionChannelMaxUrlTriggerShare implements QADescription {

    private BigDecimal threshold;
    private List<String> groups = new LinkedList<String>();
    private List<String> triggers = new LinkedList<String>();

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    @XmlElement(name = "group")
    public List<String> getGroups() {
        return groups;
    }

    @XmlElement(name = "trigger")
    public List<String> getTriggers() {
        return triggers;
    }

    @Override
    public String toString() {
        return "";
    }
}
