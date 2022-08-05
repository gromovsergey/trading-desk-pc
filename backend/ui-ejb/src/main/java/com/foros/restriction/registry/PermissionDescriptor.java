package com.foros.restriction.registry;

import com.foros.security.AccountRole;

import java.io.Serializable;

public class PermissionDescriptor implements Serializable {

    private String objectType;
    private String actionName;
    private boolean parameterized;
    private Object parameter;
    private AccountRole[] accountRoles;

    public PermissionDescriptor(String objectType, String actionName, boolean parameterized, AccountRole[] accountRoles) {
        this.objectType = objectType;
        this.actionName = actionName;
        this.parameterized = parameterized;
        this.accountRoles = accountRoles;
    }
    
    public PermissionDescriptor(String objectType, String actionName, boolean parameterized, Object parameter, AccountRole[] accountRoles) {
        this(objectType, actionName, parameterized, accountRoles);
        this.parameter = parameter;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getActionName() {
        return actionName;
    }

    public boolean isParameterized() {
        return parameterized;
    }

    public AccountRole[] getAccountRoles() {
        return accountRoles;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public Object getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        return "PermissionDescriptor{" +
                "objectType='" + objectType + '\'' +
                ", actionName='" + actionName + '\'' +
                ", parameterized=" + parameterized + '\'' +
                ", parameter=" + parameter +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionDescriptor that = (PermissionDescriptor) o;

        if (!actionName.equals(that.actionName)) return false;
        if (!objectType.equals(that.objectType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = objectType.hashCode();
        if (actionName != null) {
            result = 31 * result + actionName.hashCode();
        }
        return result;
    }
}
