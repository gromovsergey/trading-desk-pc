package com.foros.util.xml;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({QADescriptionText.class,
        QADescriptionChannelTriggerQA.class,
        QADescriptionChannelMinUrlTriggerThreshold.class,
        QADescriptionChannelMaxUrlTriggerShare.class})

@XmlRootElement(name = "description")
public class QADescriptionRoot {

    private QADescription info;

    @XmlAnyElement(lax = true)
    public QADescription getInfo() {
        return info;
    }

    public void setInfo(QADescription info) {
        this.info = info;
    }
}
