package com.foros.model.campaign;

import com.foros.persistence.hibernate.type.OptInStatusTargetingType;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;

public class OptInStatusTargeting implements Serializable {

    @RequiredConstraint
    private Boolean optedInUsers;

    @RequiredConstraint
    private Boolean optedOutUsers;

    @RequiredConstraint
    private Boolean unknownUsers;

    public OptInStatusTargeting() {
    }

    public OptInStatusTargeting(OptInStatusTargeting toCopy) {
        this(toCopy.isOptedInUsers(), toCopy.isOptedOutUsers(), toCopy.isUnknownUsers());
    }

    public OptInStatusTargeting(Boolean optedInUsers, Boolean optedOutUsers, Boolean unknownUsers) {
        this.optedInUsers = optedInUsers;
        this.optedOutUsers = optedOutUsers;
        this.unknownUsers = unknownUsers;
    }

    public boolean isEnabled() {
        return optedInUsers || optedOutUsers || unknownUsers;
    }

    public Boolean isOptedInUsers() {
        return optedInUsers;
    }

    public void setOptedInUsers(Boolean optedInUsers) {
        this.optedInUsers = optedInUsers;
    }

    public Boolean isOptedOutUsers() {
        return optedOutUsers;
    }

    public void setOptedOutUsers(Boolean optedOutUsers) {
        this.optedOutUsers = optedOutUsers;
    }

    public Boolean isUnknownUsers() {
        return unknownUsers;
    }

    public void setUnknownUsers(Boolean unknownUsers) {
        this.unknownUsers = unknownUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof OptInStatusTargeting)) {
            return false;
        }

        OptInStatusTargeting that = (OptInStatusTargeting) o;
        if (this.isOptedInUsers() != that.isOptedInUsers() ||
                this.isOptedOutUsers() != that.isOptedOutUsers() ||
                this.isUnknownUsers() != that.isUnknownUsers()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Boolean.valueOf(isOptedInUsers()).hashCode();
        hash = 31 * hash + Boolean.valueOf(isOptedOutUsers()).hashCode();
        hash = 31 * hash + Boolean.valueOf(isUnknownUsers()).hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "OptInStatusTargeting[" + OptInStatusTargetingType.toString(this) + "]";
    }

    public static OptInStatusTargeting newDefaultValue() {
        return new OptInStatusTargeting(true, false, false);
    }
}
