package com.foros.model.channel;

import com.foros.jaxb.adapters.EntityLink;
import com.foros.jaxb.adapters.TimestampXmlAdapter;
import com.foros.model.Status;
import com.foros.session.NamedTO;

import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "deviceChannel")
@XmlType(propOrder = {
        "updated",
        "status",
        "id",
        "name",
        "parentChannel",
        "expression"
})
@XmlAccessorType(XmlAccessType.NONE)
public class ApiDeviceChannelTO extends NamedTO {

    private Timestamp updated;

    private Status status;

    private EntityLink parentChannel;

    private String expression;

    public ApiDeviceChannelTO() {
    }

    public ApiDeviceChannelTO(Long id, String name) {
        super(id, name);
    }

    @Override
    @XmlElement
    public Long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @XmlElement
    @XmlJavaTypeAdapter(TimestampXmlAdapter.class)
    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    @XmlElement
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @XmlElement
    public EntityLink getParentChannel() {
        return parentChannel;
    }

    public void setParentChannel(EntityLink parentChannel) {
        this.parentChannel = parentChannel;
    }

    @XmlElement
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
