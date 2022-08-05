package com.foros.jaxb.adapters;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
        "optedInUsers",
        "optedOutUsers",
        "unknownUsers"
})
public class XmlOptInStatusTargeting implements Serializable {

    private Boolean optedInUsers;
    private Boolean optedOutUsers;
    private Boolean unknownUsers;

    public XmlOptInStatusTargeting() { }

    public XmlOptInStatusTargeting(Boolean optedInUsers, Boolean optedOutUsers, Boolean unknownUsers) {
        this.optedInUsers = optedInUsers;
        this.optedOutUsers = optedOutUsers;
        this.unknownUsers = unknownUsers;
    }

    @XmlElement(name = "allowOptedIn")
    public Boolean getOptedInUsers() {
        return optedInUsers;
    }

    public void setOptedInUsers(Boolean optedInUsers) {
        this.optedInUsers = optedInUsers;
    }

    @XmlElement(name = "allowOptedOut")
    public Boolean getOptedOutUsers() {
        return optedOutUsers;
    }

    public void setOptedOutUsers(Boolean optedOutUsers) {
        this.optedOutUsers = optedOutUsers;
    }

    @XmlElement(name = "allowUnknown")
    public Boolean getUnknownUsers() {
        return unknownUsers;
    }

    public void setUnknownUsers(Boolean unknownUsers) {
        this.unknownUsers = unknownUsers;
    }
}
