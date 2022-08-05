package com.foros.session.channel.triggerQA;

import com.foros.jaxb.adapters.QaStatusAdapter;
import com.foros.model.ApproveStatus;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.session.channel.ChannelTO;
import com.foros.util.ConditionStringBuilder;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.ObjectUtils;


@XmlRootElement(name = "qaTrigger")
@XmlType(propOrder = {
        "id",
        "originalTrigger",
        "triggerType",
        "qaStatus"
})
public class TriggerQATO extends EntityBase implements Identifiable {
    private Long id;
    private TriggerQAType triggerType;
    private String originalTrigger;
    private ApproveStatus qaStatus = ApproveStatus.HOLD;

    /**
     * The list of channels assigned to trigger through ChannelTrigger
     */
    private List<ChannelTO> channels;

    public TriggerQATO() {
    }

    public TriggerQATO(Long id, char triggerType, String originalTrigger, ApproveStatus qaStatus) {
        setId(id);
        this.triggerType = TriggerQAType.valueByLetter(triggerType);
        setOriginalTrigger(originalTrigger);
        setQaStatus(qaStatus);
    }

    @XmlElement(required = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "type", required = true)
    public TriggerQAType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerQAType triggerType) {
        this.triggerType = triggerType;
    }

    @XmlElement(required = true)
    public String getOriginalTrigger() {
        return originalTrigger;
    }

    public void setOriginalTrigger(String originalTrigger) {
        this.originalTrigger = originalTrigger;
    }

    @XmlJavaTypeAdapter(QaStatusAdapter.class)
    @XmlElement(name = "status")
    public ApproveStatus getQaStatus() {
        return qaStatus;
    }

    public void setQaStatus(ApproveStatus qaStatus) {
        this.qaStatus = qaStatus;
    }

    @XmlTransient
    public List<ChannelTO> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelTO> channels) {
        this.channels = channels;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TriggerQATO)) {
            return false;
        }

        TriggerQATO other = (TriggerQATO) obj;

        return ObjectUtils.equals(id, other.id);
    }

    @Override
    public String toString() {
        return new ConditionStringBuilder(getClass().getSimpleName())
                .append("[id=").append(id == null ? "null" : id.toString())
                .append(",qaStatus=" + qaStatus)
                .append(triggerType != null, ",triggerType=" + triggerType)
                .append(originalTrigger != null, ",originalTrigger=" + originalTrigger)
                .append("]")
                .toString();
    }
}
