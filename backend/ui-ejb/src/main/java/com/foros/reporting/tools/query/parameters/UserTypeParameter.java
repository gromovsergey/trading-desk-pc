package com.foros.reporting.tools.query.parameters;

import org.hibernate.usertype.UserType;
import org.springframework.jdbc.core.SqlParameter;

public class UserTypeParameter extends SqlParameter {

    private UserType type;

    public UserTypeParameter(String name, UserType type) {
        super(name, type.sqlTypes()[0]);
        this.type = type;
    }

    public UserType getType() {
        return type;
    }
}
