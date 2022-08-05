package com.foros.model.restriction;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "restrictionCommandsOperation")
@XmlType(propOrder = {
        "restrictionCommands"
})
public class RestrictionCommandsOperation {
    private List<RestrictionCommand> restrictionCommands;

    @XmlElementWrapper(name = "restrictionCommands")
    @XmlElement(name = "restrictionCommand")
    public List<RestrictionCommand> getRestrictionCommands() {
        return restrictionCommands;
    }

    public void setRestrictionCommands(List<RestrictionCommand> restrictionCommands) {
        this.restrictionCommands = restrictionCommands;
    }
}
