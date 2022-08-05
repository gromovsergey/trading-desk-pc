package com.foros.security;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum AccountRole {
    INTERNAL("Internal"),
    ADVERTISER("Advertiser"),
    PUBLISHER("Publisher"),
    ISP("ISP"),
    AGENCY("Agency"),
    CMP("CMP");
    
    private final String name;

    AccountRole(String name) {
        this.name = name;
    }

    public int getId() {
        return ordinal();
    }

    public String getName() {
        return name;
    }

    public String getWebName() {
        switch (this) {
            case ADVERTISER:
                return "advertiser";
            case INTERNAL:
                return "internal";
            case PUBLISHER:
                return "publisher";
            case ISP:
                return "isp";
            case AGENCY:
                return "agency";
            case CMP:
                return "cmp";
            default:
                return "unknown";
        }
    }

    public static AccountRole byName(String name) {
        for (AccountRole role : values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Illegal name given: '" + name + "'");
    }

    public static AccountRole valueOf(int i) {
        if (i < 0 || i >= AccountRole.values().length) {
            throw new IllegalArgumentException("Invalid ordinal");
        }
        return AccountRole.values()[i];
    }
}
