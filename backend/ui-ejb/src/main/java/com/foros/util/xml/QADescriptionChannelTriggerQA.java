package com.foros.util.xml;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = "channelTriggerQA")
public class QADescriptionChannelTriggerQA implements QADescription {

    private List<String> triggers = new LinkedList<String>();

    @XmlElement(name = "trigger")
    public List<String> getTriggers() {
        return triggers;
    }

    @Override
    public String toString() {
        return "\n" + StringUtils.join(triggers, "\n");
    }
}
