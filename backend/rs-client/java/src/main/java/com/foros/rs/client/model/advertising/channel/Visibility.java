package com.foros.rs.client.model.advertising.channel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "Visibility")
public enum Visibility {
    
    PUB,
    PRI,
    CMP
}