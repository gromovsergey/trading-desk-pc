package com.foros.rs.client.model.advertising.channel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "ChannelType")
public enum ChannelType {
    
    BEHAVIORAL,
    EXPRESSION,
    AUDIENCE
}